package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadAttributes() {
	val url = url("custom-generated/registries/attribute.txt")
	val attributes = getFromCacheOrDownloadTxt("attributes.txt", url).lines()

	generateAttributesEnum(attributes, url)
}

fun generateAttributesEnum(attributes: List<String>, sourceUrl: String) {
	generateEnum(
		values = attributes.map { it.replaceFirst(".", "_") },
		name = "Attributes",
		sourceUrl = sourceUrl,
		parentArgumentType = "Attribute",
		asString = "lowercase().replaceFirst(\"_\", \".\")"
	)
}
