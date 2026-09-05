package io.github.ayfri.kore.features.worldgen.heightproviders

import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns a Y level drawn from an isosceles trapezoid distribution between [minInclusive] and [maxInclusive]: the
 * levels inside the [plateau] centered on the range are equally likely, and the probability falls off linearly on
 * both sides of it.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 *
 * @property minInclusive Lowest level that can be drawn.
 * @property maxInclusive Highest level that can be drawn, which must resolve at or above [minInclusive].
 * @property plateau Height of the flat top of the distribution, `0` by default, which makes it a triangle.
 */
@Serializable
@SerialName("minecraft:trapezoid")
data class TrapezoidHeightProvider(
	var minInclusive: VerticalAnchor,
	var maxInclusive: VerticalAnchor,
	var plateau: Int? = null,
) : HeightProvider

/**
 * Creates a `trapezoid` height provider drawing between [minInclusive] and [maxInclusive], with a flat top of
 * [plateau] blocks in the middle of the range.
 *
 * ```kotlin
 * heightRange(trapezoidHeightProvider(absolute(0), absolute(128), plateau = 32))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.trapezoidHeightProvider(
	minInclusive: VerticalAnchor,
	maxInclusive: VerticalAnchor,
	plateau: Int? = null,
) = TrapezoidHeightProvider(minInclusive, maxInclusive, plateau)

/** Creates a `trapezoid` height provider drawing between the absolute Y coordinates [minInclusive] and [maxInclusive]. */
fun HeightProviderScope.trapezoidHeightProvider(
	minInclusive: Int,
	maxInclusive: Int,
	plateau: Int? = null,
) = trapezoidHeightProvider(Absolute(minInclusive), Absolute(maxInclusive), plateau)
