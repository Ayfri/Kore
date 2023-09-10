package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

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
