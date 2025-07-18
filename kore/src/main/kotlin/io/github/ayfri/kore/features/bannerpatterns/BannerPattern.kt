package io.github.ayfri.kore.features.bannerpatterns

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.types.BannerPatternArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
	val bannerPattern = BannerPattern(fileName, assetId, translationKey)
	bannerPatterns += bannerPattern
	return BannerPatternArgument(fileName, bannerPattern.namespace ?: name)
}

fun DataPack.bannerPattern(
	fileName: String = "banner_pattern",
	translationKey: String,
	assetNamespaced: String,
	assetPath: String,
): BannerPatternArgument {
	val bannerPattern = BannerPattern(fileName, BannerPatternArgument(assetPath, assetNamespaced), translationKey)
	bannerPatterns += bannerPattern
	return BannerPatternArgument(fileName, bannerPattern.namespace ?: name)
}

fun DataPack.bannerPattern(
	fileName: String = "banner_pattern",
	translationKey: String,
	assetPath: String,
) = bannerPattern(fileName, translationKey, name, assetPath)
