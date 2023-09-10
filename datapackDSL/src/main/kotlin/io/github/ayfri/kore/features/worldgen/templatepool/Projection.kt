package io.github.ayfri.kore.features.worldgen.templatepool

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Projection.Companion.ProjectionSerializer::class)
enum class Projection {
	RIGID,
	TERRAIN_MATCHING;

	companion object {
		data object ProjectionSerializer : LowercaseSerializer<Projection>(entries)
	}
}
