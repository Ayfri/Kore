package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class DesertPyramid(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.desertPyramid(
	fileName: String = "desert_pyramid",
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	init: DesertPyramid.() -> Unit = {},
): StructureArgument {
	val desertPyramid = DesertPyramid(step = step).apply(init)
	dp.structures += Structure(fileName, desertPyramid)
	return StructureArgument(fileName, desertPyramid.namespace ?: dp.name)
}
