package features.worldgen.structures.types

import arguments.types.BiomeOrTagArgument
import arguments.types.resources.StructureArgument
import arguments.types.resources.worldgen.TemplatePoolArgument
import features.worldgen.HeightMap
import features.worldgen.biome.types.Spawners
import features.worldgen.heightproviders.HeightProvider
import features.worldgen.heightproviders.constantAbsolute
import features.worldgen.structures.GenerationStep
import features.worldgen.structures.Structure
import features.worldgen.structures.StructuresBuilder
import features.worldgen.structures.TerrainAdaptation
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Jigsaw(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var startPool: TemplatePoolArgument,
	var size: Int = 0,
	var startHeight: HeightProvider = constantAbsolute(0),
	var startJigsawName: String? = null,
	var projectStartToHeightmap: HeightMap? = null,
	var maxDistanceFromCenter: Int = 80,
	var useExpansionHack: Boolean = false,
) : StructureType()

fun StructuresBuilder.jigsaw(
	filename: String = "jigsaw",
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	startPool: TemplatePoolArgument,
	init: Jigsaw.() -> Unit = {},
): StructureArgument {
	val jigsaw = Jigsaw(step = step, startPool = startPool).apply(init)
	dp.structures += Structure(filename, jigsaw)
	return StructureArgument(filename, dp.name)
}
