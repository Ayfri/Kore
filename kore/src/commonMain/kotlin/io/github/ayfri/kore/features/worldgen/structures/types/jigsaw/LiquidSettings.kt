package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * What happens to the waterloggable blocks of a jigsaw piece landing in water.
 *
 * A single pool element can override this for itself through its `overrideLiquidSettings`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable(with = LiquidSettings.Companion.LiquidSettingsSerializer::class)
enum class LiquidSettings {
	/** Waterlogs the blocks that support it, so the piece stays flooded. */
	APPLY_WATERLOGGING,

	/** Leaves the blocks dry, carving an air pocket out of the water around the piece. */
	IGNORE_WATERLOGGING;

	companion object {
		data object LiquidSettingsSerializer : LowercaseSerializer<LiquidSettings>(entries)
	}
}
