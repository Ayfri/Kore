package generators

import com.squareup.kotlinpoet.TypeSpec
import url

data class Generator(
	var name: String,
	var fileName: String,
	var argumentName: String? = name.removeSuffix("s"),
	var asString: String = "lowercase()",
	var tagsParents: Map<String, String>? = null,
	var transform: ((String) -> String)? = null,
	var additionalCode: TypeSpec.Builder.() -> Unit = {}
) {
	init {
		fileName = "${fileName.lowercase()}.txt"
	}

	var url = ""
		private set

	var enumTree = false

	fun setUrlWithType(type: String) = url("custom-generated/$type/$fileName").let { url = it }
}

fun gen(
	name: String,
	fileName: String,
	argName: String? = name.removeSuffix("s"),
	asString: String = "lowercase()",
	tagsParents: Map<String, String>? = null,
	additionalCode: TypeSpec.Builder.() -> Unit = {},
	transform: ((String) -> String)? = null
) =
	Generator(name, fileName, argName, asString, tagsParents, transform, additionalCode)

fun List<Generator>.transformRemoveJSONSuffix() = map { gen ->
	gen.apply {
		if (gen.transform == null) transform = { it.removeSuffix(".json") }
	}
}

fun List<Generator>.setUrlWithType(type: String) = map { gen ->
	gen.apply { setUrlWithType(type) }
}

fun <T> List<T>.mapIfNotNull(transform: ((T) -> T)?) = if (transform != null) map(transform) else this
