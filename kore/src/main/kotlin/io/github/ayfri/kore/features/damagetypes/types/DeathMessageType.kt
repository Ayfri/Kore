package io.github.ayfri.kore.features.damagetypes.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DeathMessageType.Companion.EffectsSerializer::class)
enum class DeathMessageType {
	DEFAULT,
	FALL_VARIANTS,
	INTENTIONAL_GAME_DESIGN;

	companion object {
		data object EffectsSerializer : LowercaseSerializer<DeathMessageType>(entries)
	}
}
