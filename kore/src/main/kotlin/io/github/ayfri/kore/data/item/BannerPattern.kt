package io.github.ayfri.kore.data.item

import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.types.resources.BannerPatternArgument
import kotlinx.serialization.Serializable

@Serializable
data class BannerPattern(
	val pattern: BannerPatternArgument,
	val color: FormattingColor,
)

fun bannerPattern(pattern: BannerPatternArgument, color: FormattingColor) = BannerPattern(pattern, color)
