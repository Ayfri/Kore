package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns an integer drawn between [minInclusive] and [maxInclusive] with a bias towards the bottom: the lower a
 * value is in the range, the more likely it is to be drawn.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 *
 * @property minInclusive Lowest value that can be drawn.
 * @property maxInclusive Highest value that can be drawn, which must be at or above [minInclusive].
 */
@Serializable
@SerialName("minecraft:biased_to_bottom")
data class BiasedToBottomIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
) : IntProvider
