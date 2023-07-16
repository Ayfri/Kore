package features.worldgen.dimension.generator

import arguments.types.resources.BiomeArgument
import features.worldgen.dimension.Dimension
import kotlinx.serialization.Serializable

@Serializable
data class Flat(
	var settings: FlatGeneratorSettings
) : Generator()

fun Dimension.flatGenerator(biome: BiomeArgument, block: FlatGeneratorSettings.() -> Unit) =
	Flat(FlatGeneratorSettings(biome).apply(block)).also { generator = it }
