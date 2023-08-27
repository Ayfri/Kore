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
data class Fortress(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.fortress(
	filename: String = "fortress",
	step: GenerationStep = GenerationStep.UNDERGROUND_DECORATION,
	init: Fortress.() -> Unit = {},
): StructureArgument {
	val fortress = Fortress(step = step).apply(init)
	dp.structures += Structure(filename, fortress)
	return StructureArgument(filename, dp.name)
}
