package generators

import generatePathEnumTree
import getFromCacheOrDownloadTxt
import url

suspend fun downloadAdvancements() {
	val url = url("data/misc/advancements.txt")
	val attributes = getFromCacheOrDownloadTxt("advancements.txt", url).lines()

	generateAdvancements(attributes, url)
}

fun generateAdvancements(attributes: List<String>, sourceUrl: String) {
	generatePathEnumTree(attributes, "Advancements", sourceUrl, "Advancement")
}
