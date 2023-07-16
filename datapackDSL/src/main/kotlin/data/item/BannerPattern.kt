package data.item

import arguments.colors.NamedColor
import arguments.types.resources.BannerPatternArgument
import kotlinx.serialization.Serializable

@Serializable
data class BannerPattern(
	val pattern: BannerPatternArgument,
	val color: NamedColor
)

fun bannerPattern(pattern: BannerPatternArgument, color: NamedColor) = BannerPattern(pattern, color)
