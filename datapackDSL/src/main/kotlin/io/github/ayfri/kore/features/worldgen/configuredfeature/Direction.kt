package io.github.ayfri.kore.features.worldgen.configuredfeature

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Direction.Companion.DirectionSerializer::class)
enum class Direction {
	UP,
	DOWN,
	NORTH,
	EAST,
	SOUTH,
	WEST;

	companion object {
		data object DirectionSerializer : LowercaseSerializer<Direction>(entries)
	}
}
