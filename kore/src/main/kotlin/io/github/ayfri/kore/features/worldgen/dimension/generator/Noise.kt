package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.BiomeSource
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseSettingsArgument
import kotlinx.serialization.Serializable

@Serializable
data class Noise(
	var settings: NoiseSettingsArgument,
	var biomeSource: BiomeSource,
) : Generator()

fun Dimension.noiseGenerator(settings: NoiseSettingsArgument, biomeSource: BiomeSource) =
	Noise(settings, biomeSource).also { generator = it }
