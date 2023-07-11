package features.worldgen.biome.types

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(with = TemperatureModifier.Companion.TemperatureModifierSerializer::class)
enum class TemperatureModifier {
	NONE,
	FROZEN;

	companion object {
		data object TemperatureModifierSerializer : LowercaseSerializer<TemperatureModifier>(entries)
	}
}
