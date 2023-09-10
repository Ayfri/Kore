package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TemperatureModifier.Companion.TemperatureModifierSerializer::class)
enum class TemperatureModifier {
	NONE,
	FROZEN;

	companion object {
		data object TemperatureModifierSerializer : LowercaseSerializer<TemperatureModifier>(entries)
	}
}
