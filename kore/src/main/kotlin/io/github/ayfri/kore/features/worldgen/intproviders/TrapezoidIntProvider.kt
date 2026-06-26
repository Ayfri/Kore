package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Int provider that samples from a trapezoid distribution spanning [min] to [max],
 * with a flat top of width [plateau] at the center where all values are equally likely.
 */
@Serializable
@SerialName("minecraft:trapezoid")
data class TrapezoidIntProvider(
	var min: Int,
	var max: Int,
	var plateau: Int,
) : IntProvider
