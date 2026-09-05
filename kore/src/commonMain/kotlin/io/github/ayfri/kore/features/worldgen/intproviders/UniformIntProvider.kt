package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns an integer drawn uniformly between [minInclusive] and [maxInclusive], both included.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 *
 * @property minInclusive Lowest value that can be drawn.
 * @property maxInclusive Highest value that can be drawn, which must be at or above [minInclusive].
 */
@Serializable
@SerialName("minecraft:uniform")
data class UniformIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
) : IntProvider
