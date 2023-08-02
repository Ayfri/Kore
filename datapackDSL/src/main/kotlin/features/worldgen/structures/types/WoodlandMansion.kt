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
data class WoodlandMansion(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.woodlandMansion(
	filename: String = "woodland_mansion",
	step: GenerationStep = GenerationStep.UNDERGROUND_DECORATION,
	init: WoodlandMansion.() -> Unit = {},
): WoodlandMansion {
	val woodlandMansion = WoodlandMansion(step = step).apply(init)
	dp.structures += Structure(filename, woodlandMansion)
	return woodlandMansion
}
