package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BiomeTemperature.Companion.BiomeTemperatureSerializer::class)
enum class BiomeTemperature {
	COLD,
	WARM;

	companion object {
		data object BiomeTemperatureSerializer : LowercaseSerializer<BiomeTemperature>(entries)
	}
}
