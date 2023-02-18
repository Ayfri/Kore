package generators

import generatePathEnumTree
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadAdvancements() {
	val url = url("custom-generated/lists/advancements.txt")
	val attributes = getFromCacheOrDownloadTxt("advancements.txt", url).lines()

	generateAdvancements(attributes, url)
}

fun generateAdvancements(attributes: List<String>, sourceUrl: String) {
	generatePathEnumTree(attributes.removeJSONSuffix(), "Advancements", sourceUrl, "Advancement")
}
