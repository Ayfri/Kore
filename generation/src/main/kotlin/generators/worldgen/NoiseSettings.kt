package generators.worldgen

import generateEnum
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadNoiseSettings() {
	val url = url("custom-generated/lists/worldgen/noise_settings.txt")
	val noiseSettings = getFromCacheOrDownloadTxt("noise_settings.txt", url).lines()

	generateNoiseSettingsEnum(noiseSettings, url)
}

fun generateNoiseSettingsEnum(noiseSettings: List<String>, sourceUrl: String) {
	generateEnum(noiseSettings.removeJSONSuffix(), "NoiseSettings", sourceUrl, "NoiseSettings")
}
