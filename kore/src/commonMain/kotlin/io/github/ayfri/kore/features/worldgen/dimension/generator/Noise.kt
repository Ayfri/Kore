package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.BiomeSource
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseSettingsArgument
import kotlinx.serialization.Serializable

/**
 * Generates terrain from a noise settings file, the generator every vanilla dimension uses.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_dimension
 *
 * @property settings The noise settings shaping the terrain.
 * @property biomeSource Where the biome of each position comes from.
 */
@Serializable
data class Noise(
	var settings: NoiseSettingsArgument,
	var biomeSource: BiomeSource,
) : Generator()

/**
 * Sets the dimension generator to a noise generator using [settings] and [biomeSource].
 *
 * ```kotlin
 * dimension("my_dimension", DimensionTypes.OVERWORLD) {
 *     noiseGenerator(NoiseSettings.OVERWORLD, multiNoise(BiomePresets.OVERWORLD))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_dimension
 */
fun Dimension.noiseGenerator(settings: NoiseSettingsArgument, biomeSource: BiomeSource) {
	generator = Noise(settings, biomeSource)
}
