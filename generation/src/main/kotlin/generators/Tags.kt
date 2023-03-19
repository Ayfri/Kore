package generators

import generatePathEnumTree
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadTags() {
	val url = url("custom-generated/lists/tags.txt")
	val tags = getFromCacheOrDownloadTxt("tags.txt", url).lines()

	generateTagsObject(tags, url)
}

fun generateTagsObject(tags: List<String>, sourceUrl: String) {
	val branchesParents = buildMap {
		put("blocks", "BlockTag")
		put("items", "ItemTag")
		put("worldgen/biome", "BiomeTag")
	}

	generatePathEnumTree(tags.removeJSONSuffix(), "Tags", sourceUrl, tagsParents = branchesParents)
}
