package data.item

import arguments.colors.FormattingColor
import arguments.types.resources.BannerPatternArgument
import kotlinx.serialization.Serializable

@Serializable
data class BannerPattern(
	val pattern: BannerPatternArgument,
	val color: FormattingColor,
)

fun bannerPattern(pattern: BannerPatternArgument, color: FormattingColor) = BannerPattern(pattern, color)
