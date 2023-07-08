package arguments.enums

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

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
