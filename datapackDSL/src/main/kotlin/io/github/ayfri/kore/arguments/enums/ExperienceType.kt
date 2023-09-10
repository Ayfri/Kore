package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(ExperienceType.Companion.ExperienceTypeSerializer::class)
enum class ExperienceType {
	LEVELS,
	POINTS;

	companion object {
		data object ExperienceTypeSerializer : LowercaseSerializer<ExperienceType>(entries)
	}
}
