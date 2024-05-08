package io.github.ayfri.kore.features.itemmodifiers.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Mode.Companion.ModeSerializer::class)
enum class Mode {
	REPLACE_ALL,
	REPLACE_SECTION,
	INSERT,
	APPEND;

	companion object {
		data object ModeSerializer : LowercaseSerializer<Mode>(entries)
	}
}
