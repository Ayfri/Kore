package features.worldgen.templatepool

import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Projection.Companion.ProjectionSerializer::class)
enum class Projection {
	RIGID,
	TERRAIN_MATCHING;

	companion object {
		data object ProjectionSerializer : LowercaseSerializer<Projection>(entries)
	}
}
