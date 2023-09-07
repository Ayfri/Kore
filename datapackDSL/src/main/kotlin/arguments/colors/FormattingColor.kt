package arguments.colors

import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FormattingColor.Companion.FormattingColorSerializer::class)
enum class FormattingColor : NamedColor {
	AQUA,
	BLACK,
	BLUE,
	DARK_AQUA,
	DARK_BLUE,
	DARK_GRAY,
	DARK_GREEN,
	DARK_PURPLE,
	DARK_RED,
	GOLD,
	GRAY,
	GREEN,
	LIGHT_PURPLE,
	RED,
	WHITE,
	YELLOW;

	override fun asString() = name.lowercase()

	companion object {
		data object FormattingColorSerializer : LowercaseSerializer<FormattingColor>(entries)
	}
}
