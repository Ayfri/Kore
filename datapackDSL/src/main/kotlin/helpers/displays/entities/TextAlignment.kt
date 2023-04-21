package helpers.displays.entities

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(TextAlignment.Companion.TextAlignmentSerializer::class)
enum class TextAlignment {
	LEFT,
	CENTER,
	RIGHT;

	companion object {
		val values = values()

		object TextAlignmentSerializer : LowercaseSerializer<TextAlignment>(values)
	}
}
