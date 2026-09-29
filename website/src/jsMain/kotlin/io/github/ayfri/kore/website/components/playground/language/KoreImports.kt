package io.github.ayfri.kore.website.components.playground.language

import io.github.ayfri.kore.website.components.playground.PlaygroundDiagnostic
import io.github.ayfri.kore.website.externals.monaco.*
import io.github.ayfri.kore.website.utils.jsObject

/** How the backend words a name nothing declares, K2's `Unresolved reference 'tellraw'.`. */
private val UNRESOLVED = Regex("""^Unresolved reference '([^']+)'""")

/** Operator conventions an import can serve with the name never written, like `set` for `this["key"] = value`. */
private val OPERATORS = setOf(
	"compareTo", "contains", "dec", "div", "divAssign", "get", "getValue", "inc", "invoke", "iterator", "minus", "minusAssign", "not", "plus",
	"plusAssign", "provideDelegate", "rangeTo", "rangeUntil", "rem", "remAssign", "set", "setValue", "times", "timesAssign", "unaryMinus", "unaryPlus",
)

/** Imports added on the fly this visit: one the visitor then deletes stays deleted. */
private val autoImported = mutableSetOf<String>()

/** A name the backend could not resolve, where it is written. */
private class Unresolved(val name: String, val line: Int, val column: Int)

/** The path importing this declaration, `null` for members, which come with their receiver. */
val ApiDeclaration.importPath get() = fqn.takeUnless { member == true || has("companion") }

/** The [paths] an import would add: not imported yet, and no other import taking the same simple name. */
fun KotlinSource.missing(paths: Collection<String>) = paths.distinct().filter { !isImported(it) && importedNames[it.substringAfterLast('.')] == null }

/**
 * One edit rewriting the import block with the [missing] of [paths] added and the imports [keep] rejects dropped:
 * sorted when the block already is, appended otherwise, and a new block under the package line or on top when there
 * is none. `null` when nothing changes.
 */
fun KotlinSource.importEdit(paths: Collection<String>, keep: (Import) -> Boolean = { true }): TextEdit? {
	val added = missing(paths)
	val kept = imports.filter(keep)
	if (added.isEmpty() && kept.size == imports.size) return null

	fun line(path: String, alias: String?) = "import $path" + (alias?.let { " as $it" } ?: "")
	val lines = kept.map { line(it.path, it.alias) } + added.map { line(it, null) }
	val block = if (kept.zipWithNext().all { (a, b) -> a.path <= b.path }) lines.sortedBy { it.removePrefix("import ") } else lines
	val packageLine = code.firstOrNull()?.takeIf { it.text == "package" }?.line

	return jsObject {
		range = jsObject {
			startLineNumber = imports.firstOrNull()?.line ?: packageLine?.plus(1) ?: 1
			startColumn = 1
			endLineNumber = imports.lastOrNull()?.line?.plus(1) ?: packageLine?.plus(1) ?: 1
			endColumn = 1
		}

		text = when {
			imports.isNotEmpty() -> block.joinToString("\n", postfix = "\n")
			packageLine != null -> block.joinToString("\n", prefix = "\n", postfix = "\n")
			else -> block.joinToString("\n", postfix = "\n\n")
		}
	}
}

/** The import a quick fix would pick without asking: the only candidate, or the only one fitting the blocks around. */
private fun KoreResolver.unambiguousImport(name: String, offset: Int): String? {
	val candidates = importCandidates(name, offset)
	val best = candidates.firstOrNull() ?: return null
	return best.declaration.importPath.takeIf { candidates.size == 1 || best.distance < candidates[1].distance }
}

private fun TextModel.offsetOf(line: Int, column: Int) = getOffsetAt(jsObject {
	lineNumber = line
	this.column = column
})

private fun TextModel.workspaceEdit(edit: TextEdit) = jsObject<WorkspaceEdit> {
	edits = arrayOf(jsObject {
		resource = uri
		textEdit = edit
		versionId = getVersionId()
	})
}

private fun Monaco.unresolvedMarkers() = editor.getModelMarkers(jsObject { owner = MARKER_OWNER }).mapNotNull { marker ->
	UNRESOLVED.find(marker.message)?.let { Unresolved(it.groupValues[1], marker.startLineNumber, marker.startColumn) }
}

/**
 * Quick fixes importing what an `Unresolved reference` marker names, one per candidate, plus "Add all missing imports"
 * over the whole file; and the `source.organizeImports` action Shift+Alt+O asks for, dropping unused imports and sorting.
 */
internal fun codeActions(monaco: Monaco, api: KoreApi, model: TextModel, context: CodeActionContext): Array<CodeAction> {
	val source = analysisOf(model)
	val resolver = KoreResolver(api, source)

	if (context.only?.startsWith("source") == true) {
		val importLines = source.imports.map { it.line }.toSet()
		val used = source.code.filter { it.type == TokenType.IDENTIFIER && it.line !in importLines }.map { it.text }.toSet()
		val edit = source.importEdit(emptyList()) { it.star || it.name in used || it.name in OPERATORS } ?: return emptyArray()

		return arrayOf(jsObject {
			title = "Optimize imports"
			kind = "source.organizeImports"
			this.edit = model.workspaceEdit(edit)
		})
	}

	val fixes = context.markers.flatMap { marker ->
		val name = UNRESOLVED.find(marker.message)?.groupValues?.get(1) ?: return@flatMap emptyList()
		val paths = resolver.importCandidates(name, model.offsetOf(marker.startLineNumber, marker.startColumn)).mapNotNull { it.declaration.importPath }.take(8)

		paths.mapNotNull { path ->
			val edit = source.importEdit(listOf(path)) ?: return@mapNotNull null
			jsObject<CodeAction> {
				title = "Import $path"
				kind = "quickfix"
				diagnostics = arrayOf(marker)
				isPreferred = paths.size == 1
				this.edit = model.workspaceEdit(edit)
			}
		}
	}

	val everything = monaco.unresolvedMarkers().mapNotNull { resolver.unambiguousImport(it.name, model.offsetOf(it.line, it.column)) }.distinct()
	val all = source.importEdit(everything)?.takeIf { everything.size > 1 }?.let { edit ->
		jsObject<CodeAction> {
			title = "Add all missing imports (${everything.size})"
			kind = "quickfix"
			this.edit = model.workspaceEdit(edit)
		}
	}

	return (fixes + listOfNotNull(all)).toTypedArray()
}

/** Imports every name only one Kore declaration fits, as one undoable edit, unless the buffer moved on from [expected]. */
private suspend fun CodeEditor.importUnambiguous(names: List<Unresolved>, expected: String, skipped: Set<String>): List<String> {
	val model = getModel() ?: return emptyList()
	val api = runCatching { KoreApi.load() }.getOrNull() ?: return emptyList()
	if (model.getValue() != expected) return emptyList()

	val source = analysisOf(model)
	val resolver = KoreResolver(api, source)
	val added = source.missing(names.mapNotNull { resolver.unambiguousImport(it.name, model.offsetOf(it.line, it.column)) }.filter { it !in skipped })
	val edit = source.importEdit(added) ?: return emptyList()

	pushUndoStop()
	executeEdits("kore-imports", arrayOf(jsObject {
		range = edit.range
		text = edit.text
	}))
	pushUndoStop()
	return added
}

/**
 * Imports on the fly what a type-check reports unresolved when only one declaration fits, returning what it imported.
 * One the visitor deletes afterwards is not imported again this visit.
 */
suspend fun CodeEditor.importOnTheFly(diagnostics: List<PlaygroundDiagnostic>, expected: String): List<String> {
	val names = diagnostics.mapNotNull { diagnostic ->
		UNRESOLVED.find(diagnostic.message)?.let { Unresolved(it.groupValues[1], diagnostic.startLine, diagnostic.startColumn) }
	}

	return importUnambiguous(names, expected, autoImported).also { autoImported += it }
}

/** The "Add missing imports" action: every unresolved name on screen with a single fitting declaration. */
internal suspend fun CodeEditor.importAllMissing(monaco: Monaco) =
	getModel()?.let { importUnambiguous(monaco.unresolvedMarkers(), it.getValue(), emptySet()) }.orEmpty()
