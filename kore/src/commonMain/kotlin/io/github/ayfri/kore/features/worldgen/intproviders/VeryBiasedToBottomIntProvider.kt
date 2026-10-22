package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.Serializable

/**
 * Returns an integer drawn between [minInclusive] and [maxInclusive] with a strong bias towards the bottom: it
 * favours the lowest values much more than [BiasedToBottomIntProvider].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 *
 * @property minInclusive Lowest value that can be drawn.
 * @property maxInclusive Highest value that can be drawn, which must be at or above [minInclusive].
 */
@Serializable
data class VeryBiasedToBottomIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
) : IntProvider
