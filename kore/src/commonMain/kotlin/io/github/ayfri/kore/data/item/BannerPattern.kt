package io.github.ayfri.kore.data.item

import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.generated.arguments.types.BannerPatternArgument
import kotlinx.serialization.Serializable

@Serializable
data class BannerPattern(
	val pattern: BannerPatternArgument,
	val color: FormattingColor,
)
