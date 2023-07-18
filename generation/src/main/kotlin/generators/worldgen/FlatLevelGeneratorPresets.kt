package generators.worldgen

import generateEnum
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadFlatLevelGeneratorPresets() {
	val url = url("custom-generated/lists/worldgen/flat_level_generator_preset.txt")
	val flatLevelGeneratorPresets = getFromCacheOrDownloadTxt("flat_level_generator_presets.txt", url).lines()

	generateFlatLevelGeneratorPresetsEnum(flatLevelGeneratorPresets, url)
}

fun generateFlatLevelGeneratorPresetsEnum(flatLevelGeneratorPresets: List<String>, sourceUrl: String) {
	generateEnum(flatLevelGeneratorPresets.removeJSONSuffix(), "FlatLevelGeneratorPresets", sourceUrl, "FlatLevelGeneratorPreset")
}
