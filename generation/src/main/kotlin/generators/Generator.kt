package generators

import com.squareup.kotlinpoet.TypeSpec
import url

/** Describes one enum/enum-tree to generate from an upstream `.txt` resource list. Build one with [gen]. */
data class Generator(
	var name: String,
	var fileName: String,
	var additionalCode: TypeSpec.Builder.() -> Unit = {},
	var argumentClassName: String? = null,
	var asString: String = $$"${name.lowercase()}",
	var enumTree: Boolean = false,
	var extractEnums: Map<String, String>? = null,
	var separator: String = "/",
	var subInterfacesParents: Map<String, String>? = null,
	var tagsParents: Map<String, String>? = null,
	var transform: ((String) -> String)? = null,
	var url: String = "",
) {
	init {
		fileName = "${fileName.lowercase()}.txt"
	}

	/** The `*Argument` interface this generator should implement, or `null` for a plain enum. */
	fun getParentArgumentType(): String? {
		var parentArgumentType = argumentClassName ?: name.removeSuffix("s")
		if (parentArgumentType.isEmpty()) return null

		if (fileName.startsWith("worldgen")) parentArgumentType = "worldgen.$parentArgumentType"
		return parentArgumentType
	}

	fun setUrlWithType(type: String) = url("custom-generated/$type/$fileName").let { url = it }
}

// Builder-style setters, so `gen(...) { ... }` call sites read like a DSL instead of assigning `var`s directly.
fun Generator.additionalCode(block: TypeSpec.Builder.() -> Unit) {
	additionalCode = block
}

fun Generator.extractEnums(vararg enums: Pair<String, String>) {
	extractEnums = enums.toMap()
}

fun Generator.subInterfacesParents(vararg interfaces: Pair<String, String>) {
	subInterfacesParents = interfaces.toMap()
}

fun Generator.tagsParents(vararg interfaces: Pair<String, String>) {
	tagsParents = interfaces.toMap()
}

fun Generator.transform(transformFunction: (String) -> String) {
	transform = transformFunction
}

fun gen(
	name: String,
	fileName: String,
	block: Generator.() -> Unit = {}
) = Generator(name, fileName).apply(block)

fun List<Generator>.transformRemoveJSONSuffix() = map { generator ->
	generator.apply {
		if (generator.transform == null) transform = { it.removeSuffix(".json") }
	}
}

fun List<Generator>.setUrlWithType(type: String) = map { generator ->
	generator.apply { setUrlWithType(type) }
}

/** Applies [transform] to every element, or returns the list unchanged when [transform] is `null`. */
fun <T> List<T>.mapIfNotNull(transform: ((T) -> T)?) = if (transform != null) map(transform) else this
