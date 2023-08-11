package features.worldgen.configuredfeature

import serializers.LowercaseSerializer
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
