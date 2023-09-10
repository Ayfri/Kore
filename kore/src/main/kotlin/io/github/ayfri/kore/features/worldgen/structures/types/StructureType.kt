package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.features.worldgen.biome.types.Spawners
import io.github.ayfri.kore.features.worldgen.structures.GenerationStep
import io.github.ayfri.kore.features.worldgen.structures.TerrainAdaptation
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
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
