package generators.worldgen

import generateEnum
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadStructureSets() {
	val url = url("custom-generated/lists/worldgen/structure_set.txt")
	val structureSets = getFromCacheOrDownloadTxt("structure_set.txt", url).lines()

	generateStructureSetsEnum(structureSets, url)
}

fun generateStructureSetsEnum(structureSets: List<String>, sourceUrl: String) {
	generateEnum(structureSets.removeJSONSuffix(), "StructureSets", sourceUrl, "StructureSet")
}
