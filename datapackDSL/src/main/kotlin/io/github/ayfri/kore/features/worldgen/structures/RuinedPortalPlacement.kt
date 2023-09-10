package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RuinedPortalPlacement.Companion.RuinedPortalPlacementSerializer::class)
enum class RuinedPortalPlacement {
	ON_LAND_SURFACE,
	PARTLY_BURIED,
	ON_OCEAN_FLOOR,
	IN_MOUNTAIN,
	UNDERGROUND,
	IN_NETHER;

	companion object {
		data object RuinedPortalPlacementSerializer : LowercaseSerializer<RuinedPortalPlacement>(entries)
	}
}
