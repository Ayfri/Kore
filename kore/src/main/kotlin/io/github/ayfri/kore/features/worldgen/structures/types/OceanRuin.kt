package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class OceanRuin(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var type: MineshaftType = MineshaftType.NORMAL,
	var largeProbability: Float,
	var clusterProbability: Float,
) : StructureType()

fun StructuresBuilder.oceanRuin(
	filename: String = "ocean_ruin",
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	largeProbability: Float = 0.3f,
	clusterProbability: Float = 0.9f,
	init: OceanRuin.() -> Unit = {},
): StructureArgument {
	val oceanRuin = OceanRuin(step = step, largeProbability = largeProbability, clusterProbability = clusterProbability).apply(init)
	dp.structures += Structure(filename, oceanRuin)
	return StructureArgument(filename, oceanRuin.namespace ?: dp.name)
}
