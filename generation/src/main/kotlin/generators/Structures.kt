package generators

import generatePathEnumTree
import getFromCacheOrDownloadTxt
import removeSuffix
import url

suspend fun downloadStructures() {
	val url = url("custom-generated/lists/structures.txt")
	val structures = getFromCacheOrDownloadTxt("structures.txt", url).lines()

	generateStructuresEnum(structures, url)
}

fun generateStructuresEnum(structures: List<String>, sourceUrl: String) {
	generatePathEnumTree(structures.removeSuffix(".nbt"), "Structures", sourceUrl, "Structure")
}
