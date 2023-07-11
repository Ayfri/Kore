package generators.worldgen

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadFeatures() {
	val url = url("custom-generated/registries/worldgen/feature.txt")
	val features = getFromCacheOrDownloadTxt("features.txt", url).lines()

	generateFeaturesEnum(features, url)
}

fun generateFeaturesEnum(features: List<String>, sourceUrl: String) {
	generateEnum(features, "Features", sourceUrl, "Feature")
}
