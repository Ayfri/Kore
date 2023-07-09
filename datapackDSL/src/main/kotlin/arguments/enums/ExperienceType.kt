package arguments.enums

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(ExperienceType.Companion.ExperienceTypeSerializer::class)
enum class ExperienceType {
	LEVELS,
	POINTS;

	companion object {
		data object ExperienceTypeSerializer : LowercaseSerializer<ExperienceType>(entries)
	}
}
