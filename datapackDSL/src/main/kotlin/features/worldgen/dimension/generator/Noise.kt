package features.worldgen.dimension.generator

import arguments.types.resources.worldgen.NoiseSettingsArgument
import features.worldgen.dimension.Dimension
import features.worldgen.dimension.biomesource.BiomeSource
import kotlinx.serialization.Serializable

@Serializable
data class Noise(
	var settings: NoiseSettingsArgument,
	var biomeSource: BiomeSource,
) : Generator()

fun Dimension.noiseGenerator(settings: NoiseSettingsArgument, biomeSource: BiomeSource) =
	Noise(settings, biomeSource).also { generator = it }
