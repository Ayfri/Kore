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
data class JungleTemple(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.jungleTemple(
	filename: String = "jungle_temple",
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	init: JungleTemple.() -> Unit = {},
): StructureArgument {
	val jungleTemple = JungleTemple(step = step).apply(init)
	dp.structures += Structure(filename, jungleTemple)
	return StructureArgument(filename, dp.name)
}
