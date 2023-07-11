package generators.worldgen

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadCarvers() {
	val url = url("custom-generated/registries/worldgen/carver.txt")
	val carvers = getFromCacheOrDownloadTxt("carvers.txt", url).lines()

	generateCarversEnum(carvers, url)
}

fun generateCarversEnum(carvers: List<String>, sourceUrl: String) {
	generateEnum(carvers, "Carvers", sourceUrl, "Carver")
}
