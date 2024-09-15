package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LiquidSettings.Companion.LiquidSettingsSerializer::class)
enum class LiquidSettings {
	APPLY_WATERLOGGING,
	IGNORE_WATERLOGGING;

	companion object {
		data object LiquidSettingsSerializer : LowercaseSerializer<LiquidSettings>(entries)
	}
}
