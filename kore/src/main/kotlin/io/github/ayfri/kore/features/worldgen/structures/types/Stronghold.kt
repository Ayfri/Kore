package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.StructureArgument
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Stronghold(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.stronghold(
	filename: String = "stronghold",
	step: GenerationStep = GenerationStep.UNDERGROUND_STRUCTURES,
	init: Stronghold.() -> Unit = {},
): StructureArgument {
	val stronghold = Stronghold(step = step).apply(init)
	dp.structures += Structure(filename, stronghold)
	return StructureArgument(filename, stronghold.namespace ?: dp.name)
}
