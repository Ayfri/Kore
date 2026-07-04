package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Fortress(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.fortress(
	filename: String = "fortress",
	step: GenerationStep = GenerationStep.UNDERGROUND_DECORATION,
	init: Fortress.() -> Unit = {},
): StructureArgument {
	val fortress = Fortress(step = step).apply(init)
	dp.structures += Structure(filename, fortress)
	return StructureArgument(filename, fortress.namespace ?: dp.name)
}
