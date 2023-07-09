package features.damagetypes.types

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(with = Scaling.Companion.ScalingSerializer::class)
enum class Scaling {
	ALWAYS,
	NEVER,
	WHEN_CAUSED_BY_LIVING_NON_PLAYER;

	companion object {
		data object ScalingSerializer : LowercaseSerializer<Scaling>(entries)
	}
}
