package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadPotions() {
	val url = url("custom-generated/registries/potion.txt")
	val potions = getFromCacheOrDownloadTxt("potions.txt", url).lines()

	generatePotionsEnum(potions, url)
}

fun generatePotionsEnum(potions: List<String>, sourceUrl: String) {
	generateEnum(potions, "Potions", sourceUrl, "Potion")
}
