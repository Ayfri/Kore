package io.github.ayfri.kore.features.damagetypes.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

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
