package arguments

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(Difficulty.Companion.DifficultySerializer::class)
enum class Difficulty {
	PEACEFUL,
	EASY,
	NORMAL,
	HARD;
	
	companion object {
		val values = values()
		
		object DifficultySerializer : LowercaseSerializer<Difficulty>(values)
	}
}
