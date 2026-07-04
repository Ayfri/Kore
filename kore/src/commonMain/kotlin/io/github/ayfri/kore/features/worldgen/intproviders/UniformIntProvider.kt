package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Int provider that returns a uniformly random integer between [minInclusive] and [maxInclusive].
 * [maxInclusive] cannot be less than [minInclusive].
 */
@Serializable
@SerialName("minecraft:uniform")
data class UniformIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
) : IntProvider
