package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns an integer drawn from an isosceles trapezoid distribution between [min] and [max]: the values inside the
 * [plateau] centered on the range are equally likely, and the probability falls off linearly on both sides of it.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 *
 * @property min Lowest value that can be drawn.
 * @property max Highest value that can be drawn, which must be at or above [min].
 * @property plateau Width of the flat top of the distribution, `0` making it a triangle.
 */
@Serializable
@SerialName("minecraft:trapezoid")
data class TrapezoidIntProvider(
	var min: Int,
	var max: Int,
	var plateau: Int,
) : IntProvider
