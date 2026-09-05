package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import kotlinx.serialization.Serializable

/**
 * Generates the debug world, a flat grid showing every block state, with no terrain and no entities.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Debug_mode
 */
@Serializable
data object Debug : Generator()

/**
 * Sets the dimension generator to the debug world.
 *
 * ```kotlin
 * dimension("debug_world", DimensionTypes.OVERWORLD) {
 *     debugGenerator()
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Debug_mode
 */
fun Dimension.debugGenerator() {
	generator = Debug
}
