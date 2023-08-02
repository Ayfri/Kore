package features.worldgen.structures.types

import arguments.types.BiomeOrTagArgument
import features.worldgen.biome.types.Spawners
import features.worldgen.structures.GenerationStep
import features.worldgen.structures.Structure
import features.worldgen.structures.StructuresBuilder
import features.worldgen.structures.TerrainAdaptation
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class SwampHut(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.swampHut(
	filename: String = "swamp_hut",
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	init: SwampHut.() -> Unit = {},
): SwampHut {
	val swampHut = SwampHut(step = step).apply(init)
	dp.structures += Structure(filename, swampHut)
	return swampHut
}
