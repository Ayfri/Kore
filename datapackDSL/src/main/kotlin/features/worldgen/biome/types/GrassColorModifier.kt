package features.worldgen.biome.types

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(with = GrassColorModifier.Companion.GrassColorModifierSerializer::class)
enum class GrassColorModifier {
	NONE,
	DARK_FOREST,
	SWAMP;

	companion object {
		data object GrassColorModifierSerializer : LowercaseSerializer<GrassColorModifier>(entries)
	}
}
