package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.StructureArgument
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class JungleTemple(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.jungleTemple(
	filename: String = "jungle_temple",
	step: GenerationStep = io.github.ayfri.kore.features.worldgen.structures.GenerationStep.SURFACE_STRUCTURES,
	init: JungleTemple.() -> Unit = {},
): StructureArgument {
	val jungleTemple = JungleTemple(step = step).apply(init)
	dp.structures += Structure(filename, jungleTemple)
	return StructureArgument(filename, dp.name)
}
