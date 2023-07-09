package data.item

import arguments.Argument
import arguments.colors.NamedColor
import kotlinx.serialization.Serializable

@Serializable
data class BannerPattern(
	val pattern: Argument.BannerPattern,
	val color: NamedColor
)
