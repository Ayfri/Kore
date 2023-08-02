package features.worldgen.structures

import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TerrainAdaptation.Companion.TerrainAdaptationSerializer::class)
enum class TerrainAdaptation {
	NONE,
	BEARD_THIN,
	BEARD_BOX,
	BURY;

	companion object {
		data object TerrainAdaptationSerializer : LowercaseSerializer<TerrainAdaptation>(entries)
	}
}
