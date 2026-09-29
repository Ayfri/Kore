package io.github.ayfri.kore.website.components.playground.language

import kotlinx.browser.window
import kotlinx.coroutines.await

/** Where [ApiDeclaration.source] paths point, the branch the site is built from. */
private const val SOURCE_URL = "https://github.com/Ayfri/Kore/blob/master/"

private val CLASSIFIER_KINDS = setOf("annotation", "class", "enum", "interface", "object", "typealias")

/** One parameter of [ApiDeclaration.params], as `:playground-examples:generatePlaygroundApiIndex` writes it. */
external interface ApiParameter {
	val default: String?
	val doc: String?
	val name: String
	val type: String
	val vararg: Boolean?
}

/**
 * A public declaration of kore, oop or helpers from `/playground-api.json`, see `ApiDeclaration` in `:playground-examples`.
 *
 * Fully qualified names ([receiver], [context], [returns], [lambda], [supertypes]) drive receiver matching, the rest is
 * shown as is.
 */
external interface ApiDeclaration {
	val companion: String?
	val constructors: Array<Array<ApiParameter>>?
	val context: Array<String>?
	val deprecated: Boolean?
	val doc: String?
	val entries: Array<String>?
	val kind: String
	val lambda: String?
	val member: Boolean?
	val modifiers: Array<String>?
	val name: String
	val owner: String
	val params: Array<ApiParameter>?
	val receiver: String?
	val receiverType: String?
	val returns: String?
	val source: String?
	val supertypes: Array<String>?
	val type: String?
	val typeParameters: String?
}

val ApiDeclaration.fqn get() = "$owner.$name"

/** Equal for a member and its overrides, and for same-looking extensions of which the closest receiver wins. */
val ApiDeclaration.overrideKey get() = "$kind $name${params?.render().orEmpty()}"
val ApiDeclaration.isClassifier get() = kind in CLASSIFIER_KINDS
val ApiDeclaration.isFunction get() = kind == "function"
val ApiDeclaration.sourceUrl get() = source?.let { SOURCE_URL + it }
fun ApiDeclaration.has(modifier: String) = modifiers?.contains(modifier) == true

/** The part of a signature between the name and the return type, `(targets: EntityArgument, text: String = "")`. */
fun Array<out ApiParameter>.render() = joinToString(prefix = "(", postfix = ")") { it.render() }

fun ApiParameter.render() = (if (vararg == true) "vararg " else "") + "$name: $type" + (default?.let { " = $it" } ?: "")

/** The declaration as Kotlin source would read it, parameters one per line once the signature gets too long for a popup. */
fun ApiDeclaration.signature(): String {
	val contextPrefix = context?.takeIf { it.isNotEmpty() }?.joinToString(prefix = "context(", postfix = ")\n") { it.substringAfterLast('.') }.orEmpty()
	val modifiersPrefix = modifiers?.filter { it != "var" && it != "const" }?.joinToString("") { "$it " }.orEmpty()
	val generics = typeParameters?.let { "$it " }.orEmpty()
	val receiverPrefix = receiverType?.let { "$it." }.orEmpty()

	return contextPrefix + modifiersPrefix + when (kind) {
		"function" -> {
			val parameters = params.orEmpty()
			val oneLine = parameters.render()
			val list = if (oneLine.length > 60 && parameters.size > 1) parameters.joinToString(",\n\t", "(\n\t", ",\n)") { it.render() } else oneLine
			"fun $generics$receiverPrefix$name$list" + (type?.takeIf { it != "Unit" }?.let { ": $it" } ?: "")
		}

		"property" -> (if (has("const")) "const val " else if (has("var")) "var " else "val ") + "$generics$receiverPrefix$name: $type"
		"typealias" -> "typealias $name${typeParameters.orEmpty()} = $type"

		else -> {
			val keyword = when (kind) {
				"annotation" -> "annotation class"
				"enum" -> "enum class"
				else -> kind
			}

			val parents = supertypes.orEmpty().filter { it != "kotlin.Enum" }.map { it.substringAfterLast('.') }
			"$keyword $name${typeParameters.orEmpty()}" + if (parents.isEmpty()) "" else parents.joinToString(prefix = " : ")
		}
	}
}

/** Hover and completion documentation: the signature, the KDoc, then where the declaration lives. */
fun ApiDeclaration.markdown(overloads: Int = 0) = buildString {
	append("```kotlin\n").append(signature()).append("\n```\n\n")
	doc?.let { append(it).append("\n\n") }
	if (overloads > 0) append("*+$overloads overload${if (overloads > 1) "s" else ""}*\n\n")
	append("`$owner`")
	sourceUrl?.let { append(" · [Source]($it)") }
}

/**
 * The playground's view of the Kore API: every declaration by name, members by owner, extensions by receiver and
 * classifiers by fully qualified name.
 */
class KoreApi(declarations: Array<ApiDeclaration>) {
	val byName = declarations.groupBy { it.name }
	val classifiers = declarations.filter { it.isClassifier }.associateBy { it.fqn }

	/** Importable declarations used without a receiver: classifiers (companions aside) and top-level functions and properties. */
	val topLevel = declarations.filter { it.member != true && it.receiver == null && it.modifiers?.contains("companion") != true }

	/** Every package, and every package prefix, holding a top-level declaration, for `import` completion. */
	val packages = declarations.filter { it.member != true && it.owner !in classifiers }.flatMap { declaration ->
		declaration.owner.split('.').runningReduce { prefix, part -> "$prefix.$part" }
	}.toSet()

	private val members = declarations.filter { it.member == true }.groupBy { it.owner }
	private val extensions = declarations.filter { it.member != true && it.receiver != null }.groupBy { it.receiver!! }
	private val nested = declarations.filter { it.isClassifier && it.owner in classifiers }.groupBy { it.owner }

	/** How often a class is a receiver or a block's `this`, which tells the DSL's `Function` from a test environment's. */
	private val popularity = declarations.flatMap { listOfNotNull(it.receiver, it.lambda) }.groupingBy { it }.eachCount()
	private val closures = HashMap<String, Set<String>>()

	fun popularity(fqn: String) = popularity[fqn] ?: 0

	/** [fqn] and everything it extends, type aliases expanded, `kotlin.Any` included since extensions on it fit anything. */
	fun supertypes(fqn: String): Set<String> = closures.getOrPut(fqn) {
		val classifier = classifiers[fqn]
		val parents = if (classifier?.kind == "typealias") listOfNotNull(classifier.returns) else classifier?.supertypes.orEmpty().toList()
		setOf(fqn, "kotlin.Any") + parents.flatMap { supertypes(it) }
	}

	/** Members and extensions callable on an instance of any of [types], member extensions aside. */
	fun callablesOn(types: Set<String>) = types.flatMap(::supertypes).toSet().flatMap { type ->
		members[type].orEmpty().filter { it.receiver == null } + extensions[type].orEmpty()
	}

	/** Member extensions of the [scopes] classes whose receiver fits [types], like `lessThanOrEqualTo` on a score inside `scores { }`. */
	fun memberExtensions(scopes: Set<String>, types: Set<String>): List<ApiDeclaration> {
		val fitting = types.flatMap(::supertypes).toSet()
		return scopes.flatMap(::supertypes).toSet().flatMap { members[it].orEmpty() }.filter { it.receiver in fitting }
	}

	/** What `Items.` offers: enum entries are handled apart, this is nested classifiers and companion or object members. */
	fun staticMembers(fqn: String): List<ApiDeclaration> {
		val classifier = classifiers[fqn] ?: return emptyList()
		val holders = listOfNotNull(classifier.companion, fqn.takeIf { classifier.kind == "object" })
		return nested[fqn].orEmpty().filter { !it.has("companion") } + holders.flatMap { callablesOn(setOf(it)) }
	}

	/** The classifier a simple name most likely means: imported, then the one the DSL uses most. */
	fun classifier(name: String, imports: Map<String, String>) = imports[name]?.let(classifiers::get)
		?: byName[name].orEmpty().filter { it.isClassifier }.maxByOrNull { popularity(it.fqn) }

	companion object {
		private var loading: KoreApi? = null

		/** Fetches `/playground-api.json` once per visit; a failed fetch is retried on the next call. */
		suspend fun load() = loading ?: run {
			val response = window.fetch("/playground-api.json").await()
			if (!response.ok) error("The Kore API index answered ${response.status}.")
			KoreApi(response.json().await().asDynamic().declarations.unsafeCast<Array<ApiDeclaration>>()).also { loading = it }
		}
	}
}
