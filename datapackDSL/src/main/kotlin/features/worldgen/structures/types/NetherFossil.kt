package features.worldgen.structures.types

import arguments.types.BiomeOrTagArgument
import features.worldgen.biome.types.Spawners
import features.worldgen.heightproviders.HeightProvider
import features.worldgen.heightproviders.constantAbsolute
import features.worldgen.structures.GenerationStep
import features.worldgen.structures.Structure
import features.worldgen.structures.StructuresBuilder
import features.worldgen.structures.TerrainAdaptation
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class NetherFossil(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var height: HeightProvider,
) : StructureType()

fun StructuresBuilder.netherFossil(
	filename: String = "nether_fossil",
	step: GenerationStep = GenerationStep.UNDERGROUND_DECORATION,
	height: HeightProvider = constantAbsolute(0),
	init: NetherFossil.() -> Unit = {},
): NetherFossil {
	val netherFossil = NetherFossil(step = step, height = height).apply(init)
	dp.structures += Structure(filename, netherFossil)
	return netherFossil
}
