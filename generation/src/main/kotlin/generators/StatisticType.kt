package generators

import generateEnum
import getFromCacheOrDownloadTxt
import removeMinecraftPrefix
import url

suspend fun downloadStatisticTypes() {
	val url = url("custom-generated/registries/stat_type.txt")
	val statisticTypes = getFromCacheOrDownloadTxt("statistic_types.txt", url).lines()

	generateStatisticTypesEnum(statisticTypes, url)
}

fun generateStatisticTypesEnum(statisticTypes: List<String>, sourceUrl: String) {
	generateEnum(statisticTypes.removeMinecraftPrefix(), "StatisticTypes", sourceUrl, "StatisticType")
}
