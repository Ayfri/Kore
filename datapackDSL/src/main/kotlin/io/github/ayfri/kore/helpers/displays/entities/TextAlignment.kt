package io.github.ayfri.kore.helpers.displays.entities

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(TextAlignment.Companion.TextAlignmentSerializer::class)
enum class TextAlignment {
	LEFT,
	CENTER,
	RIGHT;

	companion object {
		data object TextAlignmentSerializer : LowercaseSerializer<TextAlignment>(entries)
	}
}
