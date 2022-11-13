package arguments

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(Color.Companion.ColorSerializer::class)
enum class Color {
	BLACK,
	DARK_BLUE,
	DARK_GREEN,
	DARK_AQUA,
	DARK_RED,
	DARK_PURPLE,
	GOLD,
	GRAY,
	DARK_GRAY,
	BLUE,
	GREEN,
	AQUA,
	RED,
	LIGHT_PURPLE,
	YELLOW,
	WHITE;

	companion object {
		val values = values()

		object ColorSerializer : LowercaseSerializer<Color>(values)
	}
}

@Serializable(BossBarColor.Companion.BossBarColorSerializer::class)
enum class BossBarColor {
	BLUE,
	GREEN,
	PINK,
	PURPLE,
	RED,
	WHITE,
	YELLOW;

	companion object {
		val values = values()

		object BossBarColorSerializer : LowercaseSerializer<BossBarColor>(values)
	}
}
