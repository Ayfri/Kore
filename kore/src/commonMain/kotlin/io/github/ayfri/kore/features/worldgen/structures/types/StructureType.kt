package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.GenerationStep
import io.github.ayfri.kore.features.worldgen.structures.SpawnOverrides
import io.github.ayfri.kore.features.worldgen.structures.TerrainAdaptation
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * The algorithm building a [io.github.ayfri.kore.features.worldgen.structures.Structure], and the fields every
 * structure shares whatever that algorithm is.
 *
 * Only [Jigsaw] assembles its shape from data; every other type runs a hardcoded algorithm and exposes a handful of
 * knobs on top of these shared fields.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property biomes The biomes, or biome tags, the structure start is allowed to land in. Empty means nowhere.
 * @property step The decoration step stamping the pieces of the structure.
 * @property spawnOverrides The mob spawns replacing the biome ones inside the structure, empty keeping the biome ones.
 * @property terrainAdaptation How the terrain reacts around the structure, `null` meaning [TerrainAdaptation.NONE].
 */
@GeneratedSealedSerializer
@Serializable(StructureType.Companion.StructureTypeSerializer::class)
sealed class StructureType {
	abstract var biomes: InlinableList<BiomeOrTagArgument>
	abstract var step: GenerationStep
	abstract var spawnOverrides: SpawnOverrides
	abstract var terrainAdaptation: TerrainAdaptation?

	companion object {
		@OptIn(InternalSerializationApi::class)
		data object StructureTypeSerializer :
			NamespacedPolymorphicSerializer<StructureType>(structureTypeSealedSerializer())
	}
}

/**
 * Restricts the structure to [biomes], each entry being a biome or a biome tag.
 *
 * A single entry is written as a bare string instead of a one-element array.
 */
fun StructureType.biomes(vararg biomes: BiomeOrTagArgument) {
	this.biomes = biomes.toList()
}

/** Replaces the biome mob spawns inside the structure, one block per mob category. */
fun StructureType.spawnOverrides(init: SpawnOverrides.() -> Unit) {
	spawnOverrides = SpawnOverrides().apply(init)
}
