package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * The temperature variant of an [io.github.ayfri.kore.features.worldgen.structures.types.OceanRuin], picking which set
 * of templates and which loot the ruin is built from.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable(with = BiomeTemperature.Companion.BiomeTemperatureSerializer::class)
enum class BiomeTemperature {
	/** Stone brick ruins, holding drowned and cold ocean ruin loot. */
	COLD,

	/** Sandstone ruins, holding warm ocean ruin loot. */
	WARM;

	companion object {
		data object BiomeTemperatureSerializer : LowercaseSerializer<BiomeTemperature>(entries)
	}
}
