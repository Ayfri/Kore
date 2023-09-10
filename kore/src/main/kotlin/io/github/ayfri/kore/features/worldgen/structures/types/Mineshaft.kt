package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.features.worldgen.biome.types.Spawners
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Mineshaft(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var type: MineshaftType = MineshaftType.NORMAL,
) : StructureType()

fun StructuresBuilder.mineshaft(
	filename: String = "mineshaft",
	step: GenerationStep = io.github.ayfri.kore.features.worldgen.structures.GenerationStep.UNDERGROUND_STRUCTURES,
	init: Mineshaft.() -> Unit = {},
): Mineshaft {
	val mineshaft = Mineshaft(step = step).apply(init)
	dp.structures += Structure(filename, mineshaft)
	return mineshaft
}
