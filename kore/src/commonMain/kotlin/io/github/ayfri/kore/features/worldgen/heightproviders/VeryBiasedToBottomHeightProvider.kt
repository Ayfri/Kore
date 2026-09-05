package io.github.ayfri.kore.features.worldgen.heightproviders

import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns a Y level drawn between [minInclusive] and [maxInclusive] with a strong bias towards the bottom, the same
 * distribution as [BiasedToBottomHeightProvider] with a sharper exponential falloff.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 *
 * @property minInclusive Lowest level that can be drawn.
 * @property maxInclusive Highest level that can be drawn, which must resolve at or above [minInclusive].
 * @property inner Height of the uniform part at the bottom of the range, `1` by default, at most the range height.
 */
@Serializable
@SerialName("minecraft:very_biased_to_bottom")
data class VeryBiasedToBottomHeightProvider(
	var minInclusive: VerticalAnchor,
	var maxInclusive: VerticalAnchor,
	var inner: Int? = null,
) : HeightProvider

/**
 * Creates a `very_biased_to_bottom` height provider drawing between [minInclusive] and [maxInclusive], strongly
 * favouring the lower levels.
 *
 * ```kotlin
 * heightRange(veryBiasedToBottomHeightProvider(aboveBottom(8), absolute(64)))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.veryBiasedToBottomHeightProvider(
	minInclusive: VerticalAnchor,
	maxInclusive: VerticalAnchor,
	inner: Int? = null,
) = VeryBiasedToBottomHeightProvider(minInclusive, maxInclusive, inner)

/** Creates a `very_biased_to_bottom` height provider drawing between the absolute Y coordinates [minInclusive] and [maxInclusive]. */
fun HeightProviderScope.veryBiasedToBottomHeightProvider(
	minInclusive: Int,
	maxInclusive: Int,
	inner: Int? = null,
) = veryBiasedToBottomHeightProvider(Absolute(minInclusive), Absolute(maxInclusive), inner)
