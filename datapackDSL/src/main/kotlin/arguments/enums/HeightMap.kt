package commands.execute

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(HeightMap.Companion.HeightMapSerializer::class)
enum class HeightMap {
	MOTION_BLOCKING,
	MOTION_BLOCKING_NO_LEAVES,
	OCEAN_FLOOR,
	WORLD_SURFACE;

	companion object {
		data object HeightMapSerializer : LowercaseSerializer<HeightMap>(entries)
	}
}
