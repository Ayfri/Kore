package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Int provider that samples from a normal distribution defined by [mean] and [deviation],
 * then clamps the result so it is never below [minInclusive] or above [maxInclusive].
 */
@Serializable
@SerialName("minecraft:clamped_normal")
data class ClampedNormalIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
	var mean: Float,
	var deviation: Float,
) : IntProvider
