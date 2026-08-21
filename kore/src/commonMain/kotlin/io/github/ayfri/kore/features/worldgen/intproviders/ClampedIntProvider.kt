package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns the value drawn by [source], clamped so it is never below [minInclusive] nor above [maxInclusive].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 *
 * @property minInclusive Lowest value that can be returned.
 * @property maxInclusive Highest value that can be returned, which must be at or above [minInclusive].
 * @property source The int provider the value is drawn from before being clamped.
 */
@Serializable
@SerialName("minecraft:clamped")
data class ClampedIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
	var source: IntProvider,
) : IntProvider
