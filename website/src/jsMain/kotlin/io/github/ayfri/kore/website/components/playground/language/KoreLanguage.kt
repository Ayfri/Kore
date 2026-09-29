package io.github.ayfri.kore.website.components.playground.language

import io.github.ayfri.kore.website.externals.monaco.*
import io.github.ayfri.kore.website.utils.jsObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise

private const val LANGUAGE = "kotlin"

/** Owner of the compiler's markers, which the import quick fixes read back. */
const val MARKER_OWNER = "kore"

private val scope = MainScope()
private var registered = false
private var analysis: KotlinSource? = null

/** The buffer's [KotlinSource], kept while the text stays the same: every provider asks for it on each keystroke. */
internal fun analysisOf(model: TextModel): KotlinSource {
	val text = model.getValue()
	return analysis?.takeIf { it.text == text } ?: KotlinSource(text).also { analysis = it }
}

/** Runs a provider once the API index is there, answering nothing if it can't be fetched. */
private fun <T> provide(block: (KoreApi) -> T?) = scope.promise { runCatching { KoreApi.load() }.getOrNull()?.let(block) }

private fun Monaco.keyCode(code: String) = KeyCode.asDynamic()[code].unsafeCast<Int>()

private fun wordRange(line: Int, word: WordAtPosition) = jsObject<Range> {
	startLineNumber = line
	endLineNumber = line
	startColumn = word.startColumn
	endColumn = word.endColumn
}

/**
 * Registers Kotlin completion, hover docs, signature help and import fixes backed by the Kore API index, once per page
 * load since Monaco providers are global to the language. The index starts downloading right away.
 */
fun registerKoreLanguage(monaco: Monaco) {
	if (registered) return
	registered = true
	scope.launch { runCatching { KoreApi.load() } }

	val languages = monaco.languages
	languages.registerCompletionItemProvider(LANGUAGE, jsObject {
		triggerCharacters = arrayOf(".")
		provideCompletionItems = { model, position, _, _ -> provide { complete(languages, it, model, position) } }
		resolveCompletionItem = { item, _ -> item.resolve() }
	})

	languages.registerHoverProvider(LANGUAGE, jsObject {
		provideHover = { model, position, _ -> provide { hover(it, model, position) } }
	})

	languages.registerSignatureHelpProvider(LANGUAGE, jsObject {
		signatureHelpTriggerCharacters = arrayOf("(", ",")
		signatureHelpRetriggerCharacters = arrayOf(")")
		provideSignatureHelp = { model, position, _, _ -> provide { signatureHelp(it, model, position) } }
	})

	languages.registerCodeActionProvider(LANGUAGE, jsObject {
		provideCodeActions = { model, _, context, _ ->
			provide { api ->
				jsObject<CodeActionList> { actions = codeActions(monaco, api, model, context) }.also { it.asDynamic().dispose = {} }
			}
		}
	}, jsObject { providedCodeActionKinds = arrayOf("quickfix", "source.organizeImports") })
}

/** Alt+Enter opens the quick fixes like in IntelliJ, and "Add missing imports" imports every name with a single fitting declaration. */
fun CodeEditor.registerKoreActions(monaco: Monaco, onImported: (List<String>) -> Unit) {
	addAction(jsObject {
		id = "kore.quickFix"
		label = "Kore: Show quick fixes"
		keybindings = arrayOf(monaco.KeyMod.Alt or monaco.keyCode("Enter"))
		run = { editor -> editor.trigger(MARKER_OWNER, "editor.action.quickFix", null) }
	})

	addAction(jsObject {
		id = "kore.addMissingImports"
		label = "Kore: Add missing imports"
		run = { scope.launch { onImported(importAllMissing(monaco)) } }
	})
}

private fun hover(api: KoreApi, model: TextModel, position: Position): Hover? {
	val word = model.getWordAtPosition(position) ?: return null
	val source = analysisOf(model)
	val start = model.getOffsetAt(jsObject {
		lineNumber = position.lineNumber
		column = word.startColumn
	})
	if (source.inCommentOrString(start + 1)) return null

	val name = word.word
	val resolver = KoreResolver(api, source)
	val receivers = resolver.receiversAt(start)
	val previous = source.before(start)

	fun describe(found: List<ApiDeclaration>) = found.firstOrNull()?.let { first -> first.markdown(found.count { it.fqn == first.fqn && it.kind == first.kind } - 1) }

	val text = source.imports.firstOrNull { it.line == position.lineNumber }?.let { import ->
		describe(api.byName[name].orEmpty().filter { it.importPath == import.path })
	} ?: when (source.code.getOrNull(previous)?.text) {
		".", "?." -> {
			val type = source.chainEndingAt(previous - 1)?.let { resolver.typeOf(it, receivers) }
			val enum = (type as? TypeRef.Static)?.let { api.classifiers[it.fqn] }?.takeIf { it.entries?.contains(name) == true }
			enum?.let { entryMarkdown("${it.name}.$name") } ?: type?.let { describe(resolver.members(it, receivers).filter { member -> member.name == name }) }
		}

		else -> source.locals.lastOrNull { it.name == name && it.kind == LocalKind.VALUE }?.let {
			val type = resolver.typeOf(listOf(Segment(name, false)), receivers) as? TypeRef.Instance
			"```kotlin\nval $name" + (type?.types?.joinToString(" | ", prefix = ": ") { it.substringAfterLast('.') } ?: "") + "\n```"
		} ?: describe(listOfNotNull(api.classifier(name, source.importedNames)?.takeIf { name.first().isUpperCase() }))
		?: describe(resolver.inScope(name, receivers).ifEmpty { api.byName[name].orEmpty().sortedBy { it.member == true } })
	}

	return text?.let {
		jsObject {
			contents = arrayOf(markdown(it))
			range = wordRange(position.lineNumber, word)
		}
	}
}

private fun signatureOf(name: String, parameters: Array<out ApiParameter>, doc: String?): SignatureInformation {
	val offsets = mutableListOf<Array<Int>>()
	val label = buildString {
		append(name).append('(')
		parameters.forEachIndexed { index, parameter ->
			if (index > 0) append(", ")
			val start = length
			append(parameter.render())
			offsets += arrayOf(start, length)
		}
		append(')')
	}

	// Monaco checks `typeof documentation === "object"` before asserting it is defined, so a `null` there throws: leave it unset.
	return jsObject {
		this.label = label
		doc?.let { documentation = markdown(it) }
		this.parameters = parameters.mapIndexed { index, parameter ->
			jsObject<ParameterInformation> {
				this.label = offsets[index]
				parameter.doc?.let { documentation = markdown(it) }
			}
		}.toTypedArray()
	}
}

/** Every overload of the call around the caret, constructors and companion `invoke` included, the one fitting the arguments so far active. */
private fun signatureHelp(api: KoreApi, model: TextModel, position: Position): SignatureHelpResult? {
	val source = analysisOf(model)
	val offset = model.getOffsetAt(position)
	val call = source.callAt(offset) ?: return null
	val resolver = KoreResolver(api, source)
	val receivers = resolver.receiversAt(offset)

	val type = call.receiver?.let { resolver.typeOf(it, receivers) }
	val functions = when {
		call.receiver == null -> resolver.inScope(call.name, receivers)
		type != null -> resolver.members(type, receivers).filter { it.name == call.name }
		else -> emptyList()
	}.filter { it.isFunction }

	val classifier = when (type) {
		is TypeRef.Static -> api.staticMembers(type.fqn).firstOrNull { it.name == call.name && it.isClassifier }
		else -> api.classifier(call.name, source.importedNames)?.takeIf { call.receiver == null && call.name.first().isUpperCase() }
	}

	val constructors = classifier?.let { found ->
		found.constructors.orEmpty().map { Overload(found.name, it, found.doc) } +
			api.staticMembers(found.fqn).filter { it.name == "invoke" }.map { Overload(found.name, it.params.orEmpty(), it.doc) }
	}.orEmpty()

	val overloads = (functions.map { Overload(it.name, it.params.orEmpty(), it.doc) } + constructors).distinctBy { it.name + it.parameters.render() }
	if (overloads.isEmpty()) return null

	val active = overloads.indexOfFirst { overload ->
		overload.parameters.size > call.argument && (call.named == null || overload.parameters.any { it.name == call.named })
	}.coerceAtLeast(0)

	return jsObject<SignatureHelpResult> {
		value = jsObject {
			signatures = overloads.map { signatureOf(it.name, it.parameters, it.doc) }.toTypedArray()
			activeSignature = active
			activeParameter = call.named?.let { named -> overloads[active].parameters.indexOfFirst { it.name == named } } ?: call.argument
		}
	}.also { it.asDynamic().dispose = {} }
}

private class Overload(val name: String, val parameters: Array<out ApiParameter>, val doc: String?)
