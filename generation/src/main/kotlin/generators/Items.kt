package generators

import Serializer
import generateEnum
import getFromCacheOrDownloadTxt
import minecraftVersion
import url

suspend fun downloadItems() {
	val url = url("data/registries/item.txt")
	val items = getFromCacheOrDownloadTxt("items.txt", url).lines()

	generateItemsEnum(items, url)
}

fun generateItemsEnum(items: List<String>, sourceUrl: String) {
	val name = "Items"
	generateEnum(
		name = name,
		sourceUrl = sourceUrl,
		additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
		properties = items.map { it.substringAfter("minecraft:").uppercase() },
		serializer = Serializer.Lowercase,
		customEncoder = """encoder.encodeString("minecraft:${"\${value.name.lowercase()}"}")""",
		additionalImports = listOf("arguments.Argument"),
		additionalLines = arrayOf("fun item(item: $name) = Argument.Item(item.name.lowercase())")
	)
}
