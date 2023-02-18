package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadFluids() {
	val url = url("custom-generated/registries/fluid.txt")
	val fluids = getFromCacheOrDownloadTxt("fluids.txt", url).lines()

	generateFluidsObject(fluids, url)
}

fun generateFluidsObject(fluids: List<String>, sourceUrl: String) {
	generateEnum(fluids, "Fluids", sourceUrl, "Fluid")
}
