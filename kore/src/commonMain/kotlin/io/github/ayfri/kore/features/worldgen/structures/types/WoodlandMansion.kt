package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class WoodlandMansion(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
) : StructureType()

fun StructuresBuilder.woodlandMansion(
	filename: String = "woodland_mansion",
	step: GenerationStep = GenerationStep.UNDERGROUND_DECORATION,
	init: WoodlandMansion.() -> Unit = {},
): ConfiguredStructureArgument {
	val woodlandMansion = WoodlandMansion(step = step).apply(init)
	dp.structures += Structure(filename, woodlandMansion)
	return ConfiguredStructureArgument(filename, woodlandMansion.namespace ?: dp.name)
}
