package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.features.worldgen.biome.types.Spawners
import io.github.ayfri.kore.features.worldgen.structures.GenerationStep
import io.github.ayfri.kore.features.worldgen.structures.Structure
import io.github.ayfri.kore.features.worldgen.structures.StructuresBuilder
import io.github.ayfri.kore.features.worldgen.structures.TerrainAdaptation
import io.github.ayfri.kore.serializers.InlinableList
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
	step: GenerationStep = io.github.ayfri.kore.features.worldgen.structures.GenerationStep.UNDERGROUND_DECORATION,
	init: WoodlandMansion.() -> Unit = {},
): WoodlandMansion {
	val woodlandMansion = WoodlandMansion(step = step).apply(init)
	dp.structures += Structure(filename, woodlandMansion)
	return woodlandMansion
}
