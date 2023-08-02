package features.worldgen.structures.types

import arguments.types.BiomeOrTagArgument
import features.worldgen.biome.types.Spawners
import features.worldgen.structures.GenerationStep
import features.worldgen.structures.TerrainAdaptation
import serializers.InlinableList
import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(StructureType.Companion.StructureTypeSerializer::class)
sealed class StructureType {
	abstract var biomes: InlinableList<BiomeOrTagArgument>
	abstract var step: GenerationStep
	abstract var spawnOverrides: Spawners
	abstract var terrainAdaptation: TerrainAdaptation?

	companion object {
		data object StructureTypeSerializer : NamespacedPolymorphicSerializer<StructureType>(StructureType::class)
	}
}

fun StructureType.biomes(vararg biomes: BiomeOrTagArgument) {
	this.biomes = biomes.toList()
}

fun StructureType.spawnOverrides(init: Spawners.() -> Unit) {
	spawnOverrides = Spawners().apply(init)
}
