package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * The volume a [SpawnOverride] applies to.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable(with = BoundingBox.Companion.BoundingBoxSerializer::class)
enum class BoundingBox {
	/** The bounding box of the whole structure, including the empty space between its pieces. */
	FULL,

	/** The bounding box of each individual piece, so the gaps between pieces keep the biome spawns. */
	PIECE;

	companion object {
		data object BoundingBoxSerializer : LowercaseSerializer<BoundingBox>(entries)
	}
}
