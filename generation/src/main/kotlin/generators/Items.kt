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
		additionalImports = listOf("arguments.Argument", "net.benwoodworth.knbt.NbtCompound"),
		customLines = listOf(
			"override val namespace = \"minecraft\"",
			"",
			"override var nbtData: NbtCompound? = null",
			"",
			"override fun asString() = \"minecraft:\${name.lowercase()}\""
		),
		inheritances = listOf("Argument.Item"),
	)
}
