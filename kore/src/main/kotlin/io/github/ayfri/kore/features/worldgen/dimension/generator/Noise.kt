package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.arguments.types.resources.worldgen.NoiseSettingsArgument
import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.BiomeSource
import kotlinx.serialization.Serializable

@Serializable
data class Noise(
	var settings: NoiseSettingsArgument,
	var biomeSource: BiomeSource,
) : Generator()

fun Dimension.noiseGenerator(settings: NoiseSettingsArgument, biomeSource: BiomeSource) =
	Noise(settings, biomeSource).also { generator = it }
