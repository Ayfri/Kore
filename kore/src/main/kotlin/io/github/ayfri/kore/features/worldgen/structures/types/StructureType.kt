package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.BiomeArgument
import io.github.ayfri.kore.features.worldgen.structures.GenerationStep
import io.github.ayfri.kore.features.worldgen.structures.SpawnOverrides
import io.github.ayfri.kore.features.worldgen.structures.TerrainAdaptation
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(StructureType.Companion.StructureTypeSerializer::class)
sealed class StructureType {
	abstract var biomes: InlinableList<BiomeOrTagArgument>
	abstract var step: GenerationStep
	abstract var spawnOverrides: SpawnOverrides
	abstract var terrainAdaptation: TerrainAdaptation?

	companion object {
		data object StructureTypeSerializer : NamespacedPolymorphicSerializer<StructureType>(StructureType::class)
	}
}

fun StructureType.biomes(vararg biomes: BiomeArgument) {
	this.biomes = biomes.toList()
}

fun StructureType.biomes(biome: BiomeOrTagArgument) {
	biomes = listOf(biome)
}

fun StructureType.spawnOverrides(init: SpawnOverrides.() -> Unit) {
	spawnOverrides = SpawnOverrides().apply(init)
}
