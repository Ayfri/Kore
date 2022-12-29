package generators

import Serializer
import generateEnum
import getFromCacheOrDownloadTxt
import minecraftVersion
import url

suspend fun downloadAttributes() {
	val url = url("data/registries/attribute.txt")
	val attributes = getFromCacheOrDownloadTxt("attributes.txt", url).lines()

	generateAttributesEnum(attributes, url)
}

fun generateAttributesEnum(attributes: List<String>, sourceUrl: String) {
	val name = "Attributes"
	generateEnum(
		name = name,
		sourceUrl = sourceUrl,
		additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
		properties = attributes.map { it.substringAfter("minecraft:").replaceFirst(".", "_").uppercase() },
		serializer = Serializer.Lowercase,
		customEncoder = """encoder.encodeString(${"value.name.lowercase().replaceFirst(\"_\", \".\")"})""",
	)
}
