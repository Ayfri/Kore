package generators

import com.squareup.kotlinpoet.TypeSpec
import url

data class Generator(
	var name: String,
	var fileName: String,
	var argumentClassName: String? = null,
	var asString: String = $$"${name.lowercase()}",
	var tagsParents: Map<String, String>? = null,
	var transform: ((String) -> String)? = null,
	var additionalCode: TypeSpec.Builder.() -> Unit = {},
) {
	init {
		fileName = "${fileName.lowercase()}.txt"
	}

	fun getParentArgumentType(): String? {
		if (argumentClassName == null) argumentClassName = name.removeSuffix("s")
		if (argumentClassName == "") argumentClassName = null
		if (fileName.startsWith("worldgen")) argumentClassName = "worldgen.$argumentClassName"
		return argumentClassName
	}

	var url = ""
		private set

	var enumTree = false
	var separator = "/"

	fun setUrlWithType(type: String) = url("custom-generated/$type/$fileName").let { url = it }
}

fun Generator.additionalCode(block: TypeSpec.Builder.() -> Unit) {
	additionalCode = block
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

fun List<Generator>.transformRemoveMinecraftPrefix() = map { generator ->
	generator.apply {
		if (generator.transform == null) transform = { it.removePrefix("minecraft:") }
	}
}

fun List<Generator>.setUrlWithType(type: String) = map { generator ->
	generator.apply { setUrlWithType(type) }
}

fun <T> List<T>.mapIfNotNull(transform: ((T) -> T)?) = if (transform != null) map(transform) else this
