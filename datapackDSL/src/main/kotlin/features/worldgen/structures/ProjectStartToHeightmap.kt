package features.worldgen.structures

import serializers.UppercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ProjectStartToHeightmap.Companion.ProjectStartToHeightmapSerializer::class)
enum class ProjectStartToHeightmap {
	MOTION_BLOCKING,
	MOTION_BLOCKING_NO_LEAVES,
	OCEAN_FLOOR,
	OCEAN_FLOOR_WG,
	WORLD_SURFACE,
	WORLD_SURFACE_WG;

	companion object {
		data object ProjectStartToHeightmapSerializer : UppercaseSerializer<ProjectStartToHeightmap>(entries)
	}
}
