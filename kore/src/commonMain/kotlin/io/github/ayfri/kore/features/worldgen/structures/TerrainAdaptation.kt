package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * How the terrain around a structure reacts to it, so the structure does not end up cut in half by a hill.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable(with = TerrainAdaptation.Companion.TerrainAdaptationSerializer::class)
enum class TerrainAdaptation {
	/** Same as [BEARD_THIN], applied to the full bounding box of each piece instead of its shape. */
	BEARD_BOX,

	/** Raises terrain under each piece and clears the space it occupies, following the shape of the piece. */
	BEARD_THIN,

	/** Fills the space around the structure with terrain, burying it. */
	BURY,

	/** Same as [BURY], applied to the full bounding box, used by the trial chambers. */
	ENCAPSULATE,

	/** No adaptation, the structure is stamped into whatever terrain is there. */
	NONE;

	companion object {
		data object TerrainAdaptationSerializer : LowercaseSerializer<TerrainAdaptation>(entries)
	}
}
