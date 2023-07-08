package arguments.enums

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(Gamemode.Companion.GamemodeSerializer::class)
enum class Gamemode {
	SURVIVAL,
	CREATIVE,
	ADVENTURE,
	SPECTATOR;

	companion object {
		data object GamemodeSerializer : LowercaseSerializer<Gamemode>(entries)
	}
}
