package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(RenderType.Companion.RenderTypeSerializer::class)
enum class RenderType {
	HEARTS,
	INTEGER;

	companion object {
		data object RenderTypeSerializer : LowercaseSerializer<RenderType>(entries)
	}
}
