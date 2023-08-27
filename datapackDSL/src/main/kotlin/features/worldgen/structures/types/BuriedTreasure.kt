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
data class BuriedTreasure(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.buriedTreasure(
	fileName: String = "buried_treasure",
	step: GenerationStep = GenerationStep.UNDERGROUND_STRUCTURES,
	init: BuriedTreasure.() -> Unit = {},
): StructureArgument {
	val buriedTreasure = BuriedTreasure(step = step).apply(init)
	dp.structures += Structure(fileName, buriedTreasure)
	return StructureArgument(fileName, dp.name)
}
