package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadEnchantments() {
	val url = url("custom-generated/registries/enchantment.txt")
	val enchantments = getFromCacheOrDownloadTxt("enchantments.txt", url).lines()

	generateEnchantmentsEnum(enchantments, url)
}

fun generateEnchantmentsEnum(enchantments: List<String>, sourceUrl: String) {
	generateEnum(enchantments, "Enchantments", sourceUrl, "Enchantment")
}
