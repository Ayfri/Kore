package io.github.ayfri.kore.features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns a float drawn from a normal distribution of [mean] and [deviation], clamped so it is never below [min]
 * nor above [max].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 *
 * @property mean Center of the distribution.
 * @property deviation Standard deviation of the distribution, larger values spreading it wider.
 * @property min Lowest value that can be returned.
 * @property max Highest value that can be returned, which must be at or above [min].
 */
@Serializable
@SerialName("minecraft:clamped_normal")
data class ClampedNormalFloatProvider(
	var mean: Float,
	var deviation: Float,
	var min: Float,
	var max: Float,
) : FloatProvider
