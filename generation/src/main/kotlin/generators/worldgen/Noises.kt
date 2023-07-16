package generators.worldgen

import generateEnum
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadNoises() {
	val url = url("custom-generated/lists/worldgen/noise.txt")
	val noiseSettings = getFromCacheOrDownloadTxt("noises.txt", url).lines()

	generateNoiseEnum(noiseSettings, url)
}

fun generateNoiseEnum(noises: List<String>, sourceUrl: String) {
	generateEnum(noises.removeJSONSuffix(), "Noises", sourceUrl, "Noise")
}
