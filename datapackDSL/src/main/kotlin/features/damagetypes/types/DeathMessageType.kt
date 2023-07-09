package features.damagetypes.types

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(with = DeathMessageType.Companion.EffectsSerializer::class)
enum class DeathMessageType {
	DEFAULT,
	FALL_VARIANTS,
	INTENTIONAL_GAME_DESIGN;

	companion object {
		data object EffectsSerializer : LowercaseSerializer<DeathMessageType>(entries)
	}
}
