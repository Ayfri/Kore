package io.github.ayfri.kore.features.damagetypes.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Scaling.Companion.ScalingSerializer::class)
enum class Scaling {
	ALWAYS,
	NEVER,
	WHEN_CAUSED_BY_LIVING_NON_PLAYER;

	companion object {
		data object ScalingSerializer : LowercaseSerializer<Scaling>(entries)
	}
}
