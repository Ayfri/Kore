package features.worldgen

import serializers.UppercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = HeightMap.Companion.ProjectStartToHeightmapSerializer::class)
enum class HeightMap {
	MOTION_BLOCKING,
	MOTION_BLOCKING_NO_LEAVES,
	OCEAN_FLOOR,
	OCEAN_FLOOR_WG,
	WORLD_SURFACE,
	WORLD_SURFACE_WG;

	companion object {
		data object ProjectStartToHeightmapSerializer : UppercaseSerializer<HeightMap>(entries)
	}
}
