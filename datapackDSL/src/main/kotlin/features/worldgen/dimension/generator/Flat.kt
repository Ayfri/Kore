package features.worldgen.dimension.generator

import arguments.Argument
import features.worldgen.dimension.Dimension
import kotlinx.serialization.Serializable

@Serializable
data class Flat(
	var settings: FlatGeneratorSettings
) : Generator()

fun Dimension.flatGenerator(biome: Argument.Biome, block: FlatGeneratorSettings.() -> Unit) =
	Flat(FlatGeneratorSettings(biome).apply(block)).also { generator = it }
