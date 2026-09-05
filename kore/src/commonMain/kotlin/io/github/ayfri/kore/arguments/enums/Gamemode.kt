package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

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
