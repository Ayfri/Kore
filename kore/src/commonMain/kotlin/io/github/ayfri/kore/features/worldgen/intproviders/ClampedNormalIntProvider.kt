package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns an integer drawn from a normal distribution of [mean] and [deviation], clamped so it is never below
 * [minInclusive] nor above [maxInclusive].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 *
 * @property minInclusive Lowest value that can be returned.
 * @property maxInclusive Highest value that can be returned, which must be at or above [minInclusive].
 * @property mean Center of the distribution.
 * @property deviation Standard deviation of the distribution, larger values spreading it wider.
 */
@Serializable
@SerialName("minecraft:clamped_normal")
data class ClampedNormalIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
	var mean: Float,
	var deviation: Float,
) : IntProvider
