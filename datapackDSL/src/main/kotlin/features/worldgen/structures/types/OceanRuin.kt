package features.worldgen.structures.types

import arguments.types.BiomeOrTagArgument
import features.worldgen.biome.types.Spawners
import features.worldgen.structures.*
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class OceanRuin(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
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
): OceanRuin {
	val oceanRuin = OceanRuin(step = step, largeProbability = largeProbability, clusterProbability = clusterProbability).apply(init)
	dp.structures += Structure(filename, oceanRuin)
	return oceanRuin
}
