package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import kotlinx.serialization.Serializable

/**
 * Generates a superflat world, a stack of block layers over a single biome.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Superflat
 *
 * @property settings The biome, layers and structure overrides of the world.
 */
@Serializable
data class Flat(
	var settings: FlatGeneratorSettings,
) : Generator()

/**
 * Sets the dimension generator to a superflat world over [biome], configured in [block].
 *
 * ```kotlin
 * dimension("flat_world", DimensionTypes.OVERWORLD) {
 *     flatGenerator(Biomes.PLAINS) {
 *         layers {
 *             layer(Blocks.BEDROCK)
 *             layer(Blocks.DIRT, height = 2)
 *             layer(Blocks.GRASS_BLOCK)
 *         }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Superflat
 */
fun Dimension.flatGenerator(biome: BiomeArgument, block: FlatGeneratorSettings.() -> Unit = {}) {
	generator = Flat(FlatGeneratorSettings(biome).apply(block))
}
