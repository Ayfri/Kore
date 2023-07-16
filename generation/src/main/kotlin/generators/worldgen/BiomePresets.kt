package generators.worldgen

import generateEnum
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadBiomePresets() {
	val url = url("custom-generated/lists/worldgen/multi_noise_biome_source_parameter_list.txt")
	val noiseSettings = getFromCacheOrDownloadTxt("multi_noise_biome_source_parameter_list.txt", url).lines()

	generateBiomePresetsEnum(noiseSettings, url)
}

fun generateBiomePresetsEnum(noiseSettings: List<String>, sourceUrl: String) {
	generateEnum(noiseSettings.removeJSONSuffix(), "BiomePresets", sourceUrl, "BiomePreset")
}
