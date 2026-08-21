package io.github.ayfri.kore.features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns a float drawn from an isosceles trapezoid distribution between [min] and [max]: the values inside the
 * [plateau] centered on the range are equally likely, and the probability falls off linearly on both sides of it.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 *
 * @property min Lowest value that can be drawn.
 * @property max Highest value that can be drawn, which must be at or above [min].
 * @property plateau Width of the flat top of the distribution, `0` making it a triangle.
 */
@Serializable
@SerialName("minecraft:trapezoid")
data class TrapezoidFloatProvider(
	var min: Float,
	var max: Float,
	var plateau: Float,
) : FloatProvider
