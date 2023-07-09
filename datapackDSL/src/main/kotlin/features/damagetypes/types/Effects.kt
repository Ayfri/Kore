package features.damagetypes.types

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(with = Effects.Companion.EffectsSerializer::class)
enum class Effects {
	HURT,
	THORNS,
	DROWNING,
	BURNING,
	POKING,
	FREEZING;

	companion object {
		data object EffectsSerializer : LowercaseSerializer<Effects>(entries)
	}
}
