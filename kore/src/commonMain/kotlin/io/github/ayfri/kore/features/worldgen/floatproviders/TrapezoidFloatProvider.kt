package io.github.ayfri.kore.features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Float provider that samples from a trapezoid distribution spanning [min] to [max],
 * with a flat top of width [plateau] at the center where all values are equally likely.
 */
@Serializable
@SerialName("minecraft:trapezoid")
data class TrapezoidFloatProvider(
	var min: Float,
	var max: Float,
	var plateau: Float,
) : FloatProvider
