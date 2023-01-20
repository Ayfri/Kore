package generators

import Serializer
import generateEnum
import getFromCacheOrDownloadTxt
import minecraftVersion
import url

suspend fun downloadEnchantments() {
	val url = url("data/registries/enchantment.txt")
	val enchantments = getFromCacheOrDownloadTxt("enchantments.txt", url).lines()

	generateEnchantmentsEnum(enchantments, url)
}

fun generateEnchantmentsEnum(enchantments: List<String>, sourceUrl: String) {
	val name = "Enchantments"
	generateEnum(
		name = name,
		sourceUrl = sourceUrl,
		additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
		properties = enchantments.map { it.substringAfter("minecraft:").uppercase() },
		serializer = Serializer.Lowercase,
		customEncoder = """encoder.encodeString("minecraft:${"\${value.name.lowercase()}"}")""",
		additionalImports = listOf("arguments.Argument"),
		customLines = listOf(
			"override val namespace = \"minecraft\"",
			"",
			"override fun asString() = \"minecraft:\${name.lowercase()}\""
		),
		inheritances = listOf("Argument.Enchantment")
	)
}
