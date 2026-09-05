package io.github.ayfri.kore.features.bannerpatterns

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.types.BannerPatternArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven banner pattern definition.
 *
 * A banner pattern controls an additional drawable motif that players can apply on banners (and shields
 * via banner copying). It references a texture asset and a translation key, allowing you to introduce
 * new motifs that appear in the Loom UI and can be crafted/used like vanilla patterns.
 *
 * JSON format reference: https://minecraft.wiki/w/Banner/Patterns
 *
 * @param assetId - The texture asset to use for the banner pattern.
 * @param translationKey - The translation key to use for the banner pattern.
 */
@Serializable
data class BannerPattern(
	@Transient
	override var fileName: String = "banner_pattern",
	var assetId: BannerPatternArgument,
	var translationKey: String,
) : Generator("banner_pattern") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Register a new banner pattern file in the datapack.
 *
 * The provided `assetId` identifies the texture in the resource pack, while `translationKey` is shown to players in the UI.
 *
 * Produces `data/<namespace>/banner_pattern/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Banner/Patterns
 */
fun DataPack.bannerPattern(
	fileName: String = "banner_pattern",
	translationKey: String,
	assetId: BannerPatternArgument,
): BannerPatternArgument {
	val bannerPattern = BannerPattern(fileName, assetId, translationKey)
	bannerPatterns += bannerPattern
	return BannerPatternArgument(fileName, bannerPattern.namespace ?: name)
}

/**
 * Same as the other overload but lets you pass namespace and path for the asset explicitly.
 * Useful when your texture lives in another pack or namespace.
 *
 * Produces `data/<namespace>/banner_pattern/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Banner/Patterns
 */
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

/** Convenience overload that targets the current pack namespace for the texture asset. */
fun DataPack.bannerPattern(
	fileName: String = "banner_pattern",
	translationKey: String,
	assetPath: String,
) = bannerPattern(fileName, translationKey, name, assetPath)
