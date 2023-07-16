package features.worldgen.dimension.generator

import arguments.Argument
import features.worldgen.dimension.Dimension
import features.worldgen.dimension.biomesource.BiomeSource
import kotlinx.serialization.Serializable

@Serializable
data class Noise(
	var settings: Argument.NoiseSettings,
	var biomeSource: BiomeSource,
) : Generator()

fun Dimension.noiseGenerator(settings: Argument.NoiseSettings, biomeSource: BiomeSource) =
	Noise(settings, biomeSource).also { generator = it }
