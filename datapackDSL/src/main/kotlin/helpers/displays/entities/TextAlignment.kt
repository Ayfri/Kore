package helpers.displays.entities

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(TextAlignment.Companion.TextAlignmentSerializer::class)
enum class TextAlignment {
	LEFT,
	CENTER,
	RIGHT;

	companion object {
		data object TextAlignmentSerializer : LowercaseSerializer<TextAlignment>(entries)
	}
}
