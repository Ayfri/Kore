package io.github.ayfri.kore.features.bannerpatterns

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.BannerPatternArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class BannerPattern(
	@Transient
	override var fileName: String = "banner_pattern",
	var assetId: BannerPatternArgument,
	var translationKey: String,
) : Generator("banner_pattern") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.bannerPattern(
	fileName: String = "banner_pattern",
	translationKey: String,
	assetId: BannerPatternArgument,
): BannerPatternArgument {
	bannerPatterns += BannerPattern(fileName, assetId, translationKey)
	return assetId
}

fun DataPack.bannerPattern(
	fileName: String = "banner_pattern",
	translationKey: String,
	assetNamespaced: String,
	assetPath: String,
): BannerPatternArgument {
	bannerPatterns += BannerPattern(fileName, BannerPatternArgument(assetPath, assetNamespaced), translationKey)
	return BannerPatternArgument(assetPath, assetNamespaced)
}

fun DataPack.bannerPattern(
	fileName: String = "banner_pattern",
	translationKey: String,
	assetPath: String,
) = bannerPattern(fileName, translationKey, name, assetPath)
