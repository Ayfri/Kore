package io.github.ayfri.kore.features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns a float drawn uniformly in `[`[minInclusive]`, `[maxExclusive]`)`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 *
 * @property minInclusive Lowest value that can be drawn, included.
 * @property maxExclusive Upper bound of the range, excluded, which must be at or above [minInclusive].
 */
@Serializable
@SerialName("minecraft:uniform")
data class UniformFloatProvider(
	var minInclusive: Float,
	var maxExclusive: Float,
) : FloatProvider
