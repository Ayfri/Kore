package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.StructureArgument
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class SwampHut(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.swampHut(
	filename: String = "swamp_hut",
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	init: SwampHut.() -> Unit = {},
): StructureArgument {
	val swampHut = SwampHut(step = step).apply(init)
	dp.structures += Structure(filename, swampHut)
	return StructureArgument(filename, swampHut.namespace ?: dp.name)
}
