package io.github.ayfri.kore.features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Float provider that samples from a normal distribution defined by [mean] and [deviation],
 * then clamps the result so it is never below [min] or above [max].
 */
@Serializable
@SerialName("minecraft:clamped_normal")
data class ClampedNormalFloatProvider(
	var mean: Float,
	var deviation: Float,
	var min: Float,
	var max: Float,
) : FloatProvider
