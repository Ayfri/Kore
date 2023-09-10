package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.serializers.LowercaseSerializer
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
