package generators

import generateEnum
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadBiomes() {
	val url = url("custom-generated/lists/worldgen/biome.txt")
	val biomes = getFromCacheOrDownloadTxt("biomes.txt", url).lines()

	generateBiomesEnum(biomes, url)
}

fun generateBiomesEnum(biomes: List<String>, sourceUrl: String) {
	generateEnum(biomes.removeJSONSuffix(), "Biomes", sourceUrl, "Biome")
}
