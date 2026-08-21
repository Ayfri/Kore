package io.github.ayfri.kore.features.worldgen.heightproviders

import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns a Y level drawn uniformly between [minInclusive] and [maxInclusive], both included.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 *
 * @property minInclusive Lowest level that can be drawn.
 * @property maxInclusive Highest level that can be drawn, which must resolve at or above [minInclusive].
 */
@Serializable
@SerialName("minecraft:uniform")
data class UniformHeightProvider(
	var minInclusive: VerticalAnchor,
	var maxInclusive: VerticalAnchor,
) : HeightProvider

/**
 * Creates a `uniform` height provider drawing between [minInclusive] and [maxInclusive], both included.
 *
 * ```kotlin
 * cave("my_cave") {
 *     y = uniformHeightProvider(aboveBottom(8), absolute(180))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.uniformHeightProvider(
	minInclusive: VerticalAnchor,
	maxInclusive: VerticalAnchor,
) = UniformHeightProvider(minInclusive, maxInclusive)

/** Creates a `uniform` height provider drawing between the absolute Y coordinates [minInclusive] and [maxInclusive], both included. */
fun HeightProviderScope.uniformHeightProvider(
	minInclusive: Int,
	maxInclusive: Int,
) = uniformHeightProvider(Absolute(minInclusive), Absolute(maxInclusive))
