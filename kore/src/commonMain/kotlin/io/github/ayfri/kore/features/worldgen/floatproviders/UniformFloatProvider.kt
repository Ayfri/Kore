package io.github.ayfri.kore.features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Float provider that returns a uniformly random float in the range [[minInclusive], [maxExclusive]).
 * [maxExclusive] cannot be less than [minInclusive].
 */
@Serializable
@SerialName("minecraft:uniform")
data class UniformFloatProvider(
	var minInclusive: Float,
	var maxExclusive: Float,
) : FloatProvider
