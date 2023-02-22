package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadFrogVariants() {
	val url = url("custom-generated/registries/frog_variant.txt")
	val frogVariants = getFromCacheOrDownloadTxt("frog_variants.txt", url).lines()

	generateFrogVariantsEnum(frogVariants, url)
}

fun generateFrogVariantsEnum(frogVariants: List<String>, sourceUrl: String) {
	generateEnum(frogVariants, "FrogVariants", sourceUrl, "FrogVariant")
}
