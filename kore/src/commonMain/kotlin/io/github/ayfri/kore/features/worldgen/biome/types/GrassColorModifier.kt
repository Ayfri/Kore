package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = GrassColorModifier.Companion.GrassColorModifierSerializer::class)
enum class GrassColorModifier {
	NONE,
	DARK_FOREST,
	SWAMP;

	companion object {
		data object GrassColorModifierSerializer : LowercaseSerializer<GrassColorModifier>(entries)
	}
}
