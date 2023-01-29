package generators

import generatePathEnumTree
import getFromCacheOrDownloadTxt
import url

suspend fun downloadTags() {
	val url = url("data/misc/tags.txt")
	val tags = getFromCacheOrDownloadTxt("tags.txt", url).lines()

	generateTagsObject(tags, url)
}

fun generateTagsObject(tags: List<String>, sourceUrl: String) {
	generatePathEnumTree(tags, "Tags", sourceUrl)
}
