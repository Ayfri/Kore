package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadCatVariants() {
	val url = url("custom-generated/registries/cat_variant.txt")
	val catVariants = getFromCacheOrDownloadTxt("cat_variants.txt", url).lines()

	generateCatVariantsEnum(catVariants, url)
}

fun generateCatVariantsEnum(catVariants: List<String>, sourceUrl: String) {
	generateEnum(catVariants, "CatVariants", sourceUrl, "CatVariant")
}
