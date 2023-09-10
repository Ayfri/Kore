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
data class DesertPyramid(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.desertPyramid(
	fileName: String = "desert_pyramid",
	step: GenerationStep = io.github.ayfri.kore.features.worldgen.structures.GenerationStep.SURFACE_STRUCTURES,
	init: DesertPyramid.() -> Unit = {},
): StructureArgument {
	val desertPyramid = DesertPyramid(step = step).apply(init)
	dp.structures += Structure(fileName, desertPyramid)
	return StructureArgument(fileName, dp.name)
}
