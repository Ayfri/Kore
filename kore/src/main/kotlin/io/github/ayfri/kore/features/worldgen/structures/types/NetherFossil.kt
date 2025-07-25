package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.constantAbsolute
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class NetherFossil(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var height: HeightProvider,
) : StructureType()

fun StructuresBuilder.netherFossil(
	filename: String = "nether_fossil",
	step: GenerationStep = GenerationStep.UNDERGROUND_DECORATION,
	height: HeightProvider = constantAbsolute(0),
	init: NetherFossil.() -> Unit = {},
): StructureArgument {
	val netherFossil = NetherFossil(step = step, height = height).apply(init)
	dp.structures += Structure(filename, netherFossil)
	return StructureArgument(filename, netherFossil.namespace ?: dp.name)
}
