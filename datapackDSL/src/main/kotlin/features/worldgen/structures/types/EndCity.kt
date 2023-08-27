package features.worldgen.structures.types

import arguments.types.BiomeOrTagArgument
import arguments.types.resources.worldgen.StructureArgument
import features.worldgen.biome.types.Spawners
import features.worldgen.structures.GenerationStep
import features.worldgen.structures.Structure
import features.worldgen.structures.StructuresBuilder
import features.worldgen.structures.TerrainAdaptation
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class EndCity(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.endCity(
	filename: String = "end_city",
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	init: EndCity.() -> Unit = {},
): StructureArgument {
	val endCity = EndCity(step = step).apply(init)
	dp.structures += Structure(filename, endCity)
	return StructureArgument(filename, dp.name)
}
