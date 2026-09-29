package io.github.ayfri.kore.website.components.playground.language

import io.github.ayfri.kore.website.externals.monaco.*
import io.github.ayfri.kore.website.utils.jsObject

/** Qualified enum entries (`Items.STONE` for `STO`) join the list past this many upper-case letters, below it they drown it. */
private const val ENTRY_PREFIX = 2
private const val MAX_ENTRIES = 200

/** Monaco's lookup order: the leading digit of `sortText`, ties falling back to the name. */
private enum class Rank {
	LOCAL,
	INNER,
	OUTER,
	FAR,
	IMPORTED,
	LIBRARY,
	KOTLIN,
	UNFIT,
	ENTRY,
}

/** A stdlib function worth offering next to Kore's, with its snippet. [dot] ones follow an expression. */
private class Stdlib(val name: String, val snippet: String, val detail: String, val dot: Boolean = false, val infix: Boolean = false)

private val STDLIB = listOf(
	Stdlib("also", $$"also {\n\t$0\n}", " { it -> }", dot = true),
	Stdlib("apply", $$"apply {\n\t$0\n}", " { this -> }", dot = true),
	Stdlib("buildList", $$"buildList {\n\t$0\n}", " { }"),
	Stdlib("buildString", $$"buildString {\n\t$0\n}", " { }"),
	Stdlib("downTo", "downTo ", "(to: Int)", infix = true),
	Stdlib("error", $$"error($0)", "(message: Any)"),
	Stdlib("filter", $$"filter { $0 }", " { it -> Boolean }", dot = true),
	Stdlib("forEach", $$"forEach {\n\t$0\n}", " { it -> }", dot = true),
	Stdlib("forEachIndexed", $$"forEachIndexed { index, it ->\n\t$0\n}", " { index, it -> }", dot = true),
	Stdlib("let", $$"let {\n\t$0\n}", " { it -> }", dot = true),
	Stdlib("listOf", $$"listOf($0)", "(vararg elements: T)"),
	Stdlib("map", $$"map { $0 }", " { it -> R }", dot = true),
	Stdlib("mapOf", $$"mapOf($0)", "(vararg pairs: Pair<K, V>)"),
	Stdlib("mutableListOf", $$"mutableListOf($0)", "(vararg elements: T)"),
	Stdlib("mutableMapOf", $$"mutableMapOf($0)", "(vararg pairs: Pair<K, V>)"),
	Stdlib("repeat", $$"repeat($1) {\n\t$0\n}", "(times: Int) { index -> }"),
	Stdlib("require", $$"require($0)", "(value: Boolean)"),
	Stdlib("run", $$"run {\n\t$0\n}", " { }"),
	Stdlib("setOf", $$"setOf($0)", "(vararg elements: T)"),
	Stdlib("step", "step ", "(step: Int)", infix = true),
	Stdlib("takeIf", $$"takeIf { $0 }", " { it -> Boolean }", dot = true),
	Stdlib("to", "to ", "(that: B)", infix = true),
	Stdlib("until", "until ", "(to: Int)", infix = true),
	Stdlib("with", $$"with($1) {\n\t$0\n}", "(receiver: T) { }"),
)

private val SCOPE_KEYWORDS = listOf(
	"class", "data class", "else", "enum class", "false", "for", "fun", "if", "in", "is", "null", "object", "return", "this", "true", "val", "var",
	"when", "while",
)

private val KOTLIN_TYPES = listOf("Any", "Boolean", "Double", "Float", "Int", "List", "Long", "Map", "MutableList", "MutableMap", "Set", "String", "Unit")

/** Whether every character of [prefix] appears in [name] in order, ignoring case: a superset of what Monaco's fuzzy filter keeps. */
private fun matches(name: String, prefix: String): Boolean {
	var index = 0
	for (char in name) if (index < prefix.length && char.equals(prefix[index], ignoreCase = true)) index++
	return index == prefix.length
}

/** Collects one completion list. Items are filtered by [prefix] up front, Monaco then refines as the visitor types. */
private class Completions(
	private val languages: MonacoLanguages,
	private val source: KotlinSource,
	private val range: Range,
	private val prefix: String,
	private val callable: Boolean,
) {
	val items = mutableListOf<CompletionItem>()
	var incomplete = false
	private val kinds = languages.CompletionItemKind

	fun add(
		name: String,
		kind: Int,
		rank: Rank,
		insert: String = name,
		detail: String? = null,
		description: String? = null,
		filter: String? = null,
		configure: CompletionItem.() -> Unit = {},
	) {
		if (!matches(filter ?: name, prefix)) return
		// Unset rather than `null`, Monaco tells missing fields apart with `typeof`.
		items += jsObject<CompletionItem> {
			label = jsObject {
				label = name
				detail?.let { this.detail = it }
				description?.let { this.description = it }
			}
			this.kind = kind
			insertText = insert
			insertTextRules = languages.CompletionItemInsertTextRule.InsertAsSnippet
			this.range = range
			sortText = "${rank.ordinal}$name"
			filter?.let { filterText = it }
			configure()
		}
	}

	fun declaration(declaration: ApiDeclaration, rank: Rank, infix: Boolean = false) {
		if (!matches(declaration.name, prefix)) return
		val parameters = declaration.params.orEmpty()

		add(
			name = declaration.name,
			kind = kindOf(declaration),
			rank = if (declaration.deprecated == true) Rank.UNFIT else rank,
			insert = if (infix || !callable) declaration.name else declaration.snippet(),
			detail = when (declaration.kind) {
				"function" -> parameters.render()
				"property" -> ": ${declaration.type}"
				else -> declaration.typeParameters
			},
			description = declaration.receiverType ?: declaration.owner.removePrefix("io.github.ayfri.kore.").takeIf { declaration.member != true }
				?: declaration.owner.substringAfterLast('.'),
		) {
			defer(declaration.importPath, declaration = declaration)
			if (declaration.deprecated == true) tags = arrayOf(languages.CompletionItemTag.Deprecated)
			if (callable && declaration.isFunction && parameters.isNotEmpty() && !infix) command = jsObject {
				id = "editor.action.triggerParameterHints"
				title = "Show parameters"
			}
		}
	}

	/** An entry of [enum], written `Items.STONE` when [qualified], importing the enum then. */
	fun entry(enum: ApiDeclaration, entry: String, qualified: Boolean) =
		add(entry, kinds.EnumMember, if (qualified) Rank.ENTRY else Rank.INNER, if (qualified) "${enum.name}.$entry" else entry, description = enum.name) {
			defer(enum.importPath.takeIf { qualified }, entry = "${enum.name}.$entry")
		}

	/** What [resolveCompletionItem] fills in for the one item it is asked about: the docs, and the import edit Monaco applies on accept. */
	private fun CompletionItem.defer(importPath: String?, declaration: ApiDeclaration? = null, entry: String? = null) {
		val item = asDynamic()
		item.koreDeclaration = declaration
		item.koreEntry = entry
		item.koreImport = importPath
		item.koreSource = source
	}

	fun local(local: Local) = add(
		name = local.name,
		kind = when (local.kind) {
			LocalKind.CLASS -> kinds.Class
			LocalKind.FUNCTION -> kinds.Function
			LocalKind.VALUE -> kinds.Variable
		},
		rank = Rank.LOCAL,
		insert = if (local.kind == LocalKind.FUNCTION && callable) $$"$${local.name}($0)" else local.name,
		detail = local.type?.let { ": $it" },
	)

	fun stdlib(function: Stdlib) = add(function.name, kinds.Function, Rank.KOTLIN, if (callable) function.snippet else function.name, function.detail, "kotlin")
	fun keyword(keyword: String) = add(keyword, kinds.Keyword, Rank.KOTLIN, "$keyword ")

	private fun kindOf(declaration: ApiDeclaration) = when (declaration.kind) {
		"annotation", "interface" -> kinds.Interface
		"enum" -> kinds.Enum
		"function" -> if (declaration.member == true || declaration.receiver != null) kinds.Method else kinds.Function
		"object" -> kinds.Module
		"property" -> if (declaration.has("const")) kinds.Constant else kinds.Property
		"typealias" -> kinds.TypeParameter
		else -> kinds.Class
	}
}

/**
 * What accepting the declaration writes: `name()` or `name($0)`, a block for a trailing lambda the call can't do without,
 * `function($1) {}` jumping from the name to the body with Tab.
 */
private fun ApiDeclaration.snippet(): String {
	if (!isFunction) return name
	val parameters = params.orEmpty()
	val lambda = parameters.lastOrNull()?.takeIf { "->" in it.type && it.default == null }
	val arguments = if (lambda != null) parameters.dropLast(1) else parameters.toList()

	return when {
		lambda != null && arguments.none { it.default == null && it.vararg != true } -> $$"$$name {\n\t$0\n}"
		lambda != null -> $$"$$name($1) {\n\t$0\n}"
		arguments.isEmpty() -> "$name()"
		else -> $$"$$name($0)"
	}
}

/**
 * Fills the documentation and the import of the one item shown or accepted, which is what keeps thousands of items
 * cheap: Monaco resolves an item before applying its `additionalTextEdits`.
 */
internal fun CompletionItem.resolve() = apply {
	val item = asDynamic()
	val declaration = item.koreDeclaration.unsafeCast<ApiDeclaration?>()
	val entry = item.koreEntry.unsafeCast<String?>()

	when {
		declaration != null -> documentation = markdown(declaration.markdown())
		entry != null -> documentation = markdown(entryMarkdown(entry))
	}

	val path = item.koreImport.unsafeCast<String?>() ?: return@apply
	item.koreSource.unsafeCast<KotlinSource>().importEdit(listOf(path))?.let { edit ->
		additionalTextEdits = arrayOf(jsObject {
			range = edit.range
			text = edit.text
		})
	}
}

internal fun entryMarkdown(entry: String) = "```kotlin\n$entry\n```\n\n`minecraft:${entry.substringAfterLast('.').lowercase()}`"

internal fun markdown(text: String) = jsObject<MarkdownString> { value = text }

/** The completion list at [position], or `null` in a comment, a string, or where a new name is being declared. */
internal fun complete(languages: MonacoLanguages, api: KoreApi, model: TextModel, position: Position): CompletionList? {
	val source = analysisOf(model)
	val word = model.getWordUntilPosition(position)
	val caret = model.getOffsetAt(position)
	val start = caret - word.word.length
	val range = jsObject<Range> {
		startLineNumber = position.lineNumber
		endLineNumber = position.lineNumber
		startColumn = word.startColumn
		endColumn = position.column
	}

	// Completing a name already followed by its arguments rewrites the name only.
	val callable = source.text.getOrNull(caret)?.let { it == '(' || it == '{' } != true
	val completions = Completions(languages, source, range, word.word, callable)
	val resolver = KoreResolver(api, source)
	val receivers = resolver.receiversAt(start)

	fun scope() {
		source.locals.filter { source.code[it.token].start != start }.distinctBy { it.name }.forEach(completions::local)
		resolver.scope(receivers).forEach { candidate ->
			val declaration = candidate.declaration
			if (declaration.name == "invoke" || declaration.name == "Companion") return@forEach

			completions.declaration(declaration, when {
				candidate.distance == 0 && receivers.isNotEmpty() -> Rank.INNER
				candidate.distance == 1 && receivers.size > 1 -> Rank.OUTER
				candidate.distance < receivers.size -> Rank.FAR
				candidate.distance > receivers.size -> Rank.UNFIT
				declaration.importPath?.let(source::isImported) == true -> Rank.IMPORTED
				else -> Rank.LIBRARY
			})
		}

		STDLIB.filter { !it.dot && !it.infix }.forEach(completions::stdlib)
		SCOPE_KEYWORDS.forEach(completions::keyword)

		// `DIAMOND_S` offers `Items.DIAMOND_SWORD` and the like, typed in upper case to ask for them.
		if (word.word.length >= ENTRY_PREFIX && word.word.first().isUpperCase()) {
			val entries = api.classifiers.values.filter { it.kind == "enum" && it.owner !in api.classifiers }.flatMap { enum ->
				enum.entries.orEmpty().filter { matches(it, word.word) }.map { enum to it }
			}.sortedBy { (_, entry) -> !entry.startsWith(word.word, ignoreCase = true) }

			entries.take(MAX_ENTRIES).forEach { (enum, entry) -> completions.entry(enum, entry, qualified = true) }
			completions.incomplete = entries.size > MAX_ENTRIES
		}

		// A prefix that may still grow into an upper-case one has to ask again for the entries.
		else completions.incomplete = word.word.isEmpty() || word.word.first().isUpperCase()
	}

	when (val site = source.siteAt(start)) {
		Site.None -> return null
		Site.Scope -> scope()

		is Site.ImportPath -> {
			val depth = if (site.path.isEmpty()) 0 else site.path.count { it == '.' } + 1
			api.packages.filter { site.path.isEmpty() || it.startsWith("${site.path}.") }.mapNotNull { it.split('.').getOrNull(depth) }.distinct()
				.forEach { completions.add(it, languages.CompletionItemKind.Module, Rank.INNER) }
			api.topLevel.filter { it.owner == site.path }.distinctBy { it.name }.forEach { completions.add(it.name, languages.CompletionItemKind.Class, Rank.OUTER, description = it.kind) }
		}

		is Site.Infix -> {
			val type = resolver.typeOf(site.receiver, receivers) as? TypeRef.Instance
			val infix = type?.let { resolver.members(it, receivers) }.orEmpty().filter { it.has("infix") }
			if (infix.isEmpty()) scope()
			infix.forEach { completions.declaration(it, Rank.INNER, infix = true) }
			STDLIB.filter { it.infix }.forEach(completions::stdlib)
		}

		is Site.Member -> {
			when (val type = resolver.typeOf(site.receiver, receivers)) {
				is TypeRef.Static -> {
					val classifier = api.classifiers[type.fqn]
					classifier?.entries?.forEach { completions.entry(classifier, it, qualified = false) }
					resolver.members(type, receivers).forEach { completions.declaration(it, Rank.OUTER) }
				}

				is TypeRef.Instance -> resolver.members(type, receivers).forEach { completions.declaration(it, Rank.INNER) }
				null -> Unit
			}

			STDLIB.filter { it.dot }.forEach(completions::stdlib)
		}

		Site.Type -> {
			source.locals.filter { it.kind == LocalKind.CLASS }.forEach(completions::local)
			api.topLevel.filter { it.isClassifier }.forEach { completions.declaration(it, if (it.owner in api.classifiers) Rank.FAR else Rank.LIBRARY) }
			KOTLIN_TYPES.forEach { completions.add(it, languages.CompletionItemKind.Class, Rank.KOTLIN, description = "kotlin") }
		}
	}

	return jsObject {
		incomplete = completions.incomplete
		suggestions = completions.items.toTypedArray()
	}
}
