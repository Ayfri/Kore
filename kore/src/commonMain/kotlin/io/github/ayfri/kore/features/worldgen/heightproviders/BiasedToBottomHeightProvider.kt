package io.github.ayfri.kore.features.worldgen.heightproviders

import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns a Y level drawn between [minInclusive] and [maxInclusive] with a bias towards the bottom: the [inner]
 * blocks above [minInclusive] are drawn uniformly, and everything above them follows an exponential falloff.
 *
 * See [VeryBiasedToBottomHeightProvider] for the same distribution with a sharper falloff.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 *
 * @property minInclusive Lowest level that can be drawn.
 * @property maxInclusive Highest level that can be drawn, which must resolve at or above [minInclusive].
 * @property inner Height of the uniform part at the bottom of the range, `1` by default, at most the range height.
 */
@Serializable
@SerialName("minecraft:biased_to_bottom")
data class BiasedToBottomHeightProvider(
	var minInclusive: VerticalAnchor,
	var maxInclusive: VerticalAnchor,
	var inner: Int? = null,
) : HeightProvider

/**
 * Creates a `biased_to_bottom` height provider drawing between [minInclusive] and [maxInclusive], favouring the
 * lower levels.
 *
 * ```kotlin
 * heightRange(biasedToBottomHeightProvider(aboveBottom(8), absolute(64)))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.biasedToBottomHeightProvider(
	minInclusive: VerticalAnchor,
	maxInclusive: VerticalAnchor,
	inner: Int? = null,
) = BiasedToBottomHeightProvider(minInclusive, maxInclusive, inner)

/** Creates a `biased_to_bottom` height provider drawing between the absolute Y coordinates [minInclusive] and [maxInclusive]. */
fun HeightProviderScope.biasedToBottomHeightProvider(
	minInclusive: Int,
	maxInclusive: Int,
	inner: Int? = null,
) = biasedToBottomHeightProvider(Absolute(minInclusive), Absolute(maxInclusive), inner)
