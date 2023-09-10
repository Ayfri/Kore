package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.StructureArgument
import io.github.ayfri.kore.features.worldgen.biome.types.Spawners
import io.github.ayfri.kore.features.worldgen.structures.GenerationStep
import io.github.ayfri.kore.features.worldgen.structures.Structure
import io.github.ayfri.kore.features.worldgen.structures.StructuresBuilder
import io.github.ayfri.kore.features.worldgen.structures.TerrainAdaptation
import io.github.ayfri.kore.serializers.InlinableList
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
	step: GenerationStep = io.github.ayfri.kore.features.worldgen.structures.GenerationStep.UNDERGROUND_STRUCTURES,
	init: BuriedTreasure.() -> Unit = {},
): StructureArgument {
	val buriedTreasure = BuriedTreasure(step = step).apply(init)
	dp.structures += Structure(fileName, buriedTreasure)
	return StructureArgument(fileName, dp.name)
}
