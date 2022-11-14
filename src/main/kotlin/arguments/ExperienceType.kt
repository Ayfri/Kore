package arguments

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(ExperienceType.Companion.ExperienceTypeSerializer::class)
enum class ExperienceType {
	LEVELS,
	POINTS;
	
	companion object {
		val values = values()
		
		object ExperienceTypeSerializer : LowercaseSerializer<ExperienceType>(values)
	}
}
