package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(Difficulty.Companion.DifficultySerializer::class)
enum class Difficulty {
	PEACEFUL,
	EASY,
	NORMAL,
	HARD;

	companion object {
		data object DifficultySerializer : LowercaseSerializer<Difficulty>(entries)
	}
}
