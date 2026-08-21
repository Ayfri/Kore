package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * The decoration step a structure is placed during, relative to the features of the biome it lands in.
 *
 * A structure start is always computed before the terrain of its chunks, but its pieces are stamped during this step,
 * so everything placed by a later step decorates on top of the structure.
 *
 * The entries are declared in generation order rather than alphabetically, matching the slots of
 * [io.github.ayfri.kore.features.worldgen.biome.types.Features].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable(with = GenerationStep.Companion.GenerationStepSerializer::class)
enum class GenerationStep {
	/** Raw terrain shaping, before anything else. */
	RAW_GENERATION,

	/** Lava and water lakes. */
	LAKES,

	/** Small terrain edits such as icebergs, geodes and dripstone clusters. */
	LOCAL_MODIFICATIONS,

	/** Underground structures such as mineshafts and dungeons. */
	UNDERGROUND_STRUCTURES,

	/** Surface structures such as villages, temples and ruined portals. */
	SURFACE_STRUCTURES,

	/** Strongholds. */
	STRONGHOLDS,

	/** Ore veins and other underground blobs. */
	UNDERGROUND_ORES,

	/** Underground decoration such as glow lichen, sculk patches and monster rooms. */
	UNDERGROUND_DECORATION,

	/** Water and lava springs. */
	FLUID_SPRINGS,

	/** Vegetation: trees, grass, flowers, kelp. */
	VEGETAL_DECORATION,

	/** Final surface pass, mainly freezing water and adding snow. */
	TOP_LAYER_MODIFICATION;

	companion object {
		data object GenerationStepSerializer : LowercaseSerializer<GenerationStep>(entries)
	}
}
