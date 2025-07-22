package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DyeColors.Companion.DyeColorsSerializer::class)
enum class DyeColors {
	BLACK,
	BLUE,
	BROWN,
	CYAN,
	GRAY,
	GREEN,
	LIGHT_BLUE,
	LIGHT_GRAY,
	LIME,
	MAGENTA,
	ORANGE,
	PINK,
	PURPLE,
	RED,
	WHITE,
	YELLOW;

	companion object {
		data object DyeColorsSerializer : LowercaseSerializer<DyeColors>(entries)
	}
}
