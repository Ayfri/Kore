package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
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
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	init: JungleTemple.() -> Unit = {},
): StructureArgument {
	val jungleTemple = JungleTemple(step = step).apply(init)
	dp.structures += Structure(filename, jungleTemple)
	return StructureArgument(filename, jungleTemple.namespace ?: dp.name)
}
