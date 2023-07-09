package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadBannerPatterns() {
	val url = url("custom-generated/registries/banner_pattern.txt")
	val bannerPatterns = getFromCacheOrDownloadTxt("banner_pattern.txt", url).lines()

	generateBannerPatternEnum(bannerPatterns, url)
}

fun generateBannerPatternEnum(bannerPatterns: List<String>, sourceUrl: String) {
	generateEnum(bannerPatterns, "BannerPatterns", sourceUrl, "BannerPattern")
}
