package generators.worldgen

import generatePathEnumTree
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadDensityFunctions() {
	val url = url("custom-generated/lists/worldgen/density_function.txt")
	val densityFunctions = getFromCacheOrDownloadTxt("density_function.txt", url).lines()

	generateDensityFunctionsEnum(densityFunctions, url)
}

fun generateDensityFunctionsEnum(densityFunctions: List<String>, sourceUrl: String) {
	generatePathEnumTree(densityFunctions.removeJSONSuffix(), "DensityFunctions", sourceUrl, "DensityFunction")
}
