package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class ShipWreck(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var isBeached: Boolean? = null,
) : StructureType()

fun StructuresBuilder.shipWreck(
	filename: String = "shipwreck",
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	init: ShipWreck.() -> Unit = {},
): ConfiguredStructureArgument {
	val shipWreck = ShipWreck(step = step).apply(init)
	dp.structures += Structure(filename, shipWreck)
	return ConfiguredStructureArgument(filename, shipWreck.namespace ?: dp.name)
}
