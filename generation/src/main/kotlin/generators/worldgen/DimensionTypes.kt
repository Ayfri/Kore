package generators.worldgen

import generateEnum
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadDimensionTypes() {
	val url = url("custom-generated/lists/dimension_types.txt")
	val dimensionTypes = getFromCacheOrDownloadTxt("dimension_types.txt", url).lines()

	generateDimensionTypesEnum(dimensionTypes, url)
}

fun generateDimensionTypesEnum(dimensionTypes: List<String>, sourceUrl: String) {
	generateEnum(dimensionTypes.removeJSONSuffix(), "DimensionTypes", sourceUrl, "DimensionType")
}
