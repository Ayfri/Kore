package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Where a [io.github.ayfri.kore.features.worldgen.structures.types.RuinedPortalSetup] places its portal, which also
 * decides the height it is looked up at.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable(with = RuinedPortalPlacement.Companion.RuinedPortalPlacementSerializer::class)
enum class RuinedPortalPlacement {
	/** High up, on the side of a mountain. */
	IN_MOUNTAIN,

	/** Anywhere in the Nether, the only placement its dimension supports. */
	IN_NETHER,

	/** On top of the terrain. */
	ON_LAND_SURFACE,

	/** On the floor of an ocean. */
	ON_OCEAN_FLOOR,

	/** On top of the terrain, sunk a few blocks into it. */
	PARTLY_BURIED,

	/** Fully underground, in a cave. */
	UNDERGROUND;

	companion object {
		data object RuinedPortalPlacementSerializer : LowercaseSerializer<RuinedPortalPlacement>(entries)
	}
}
