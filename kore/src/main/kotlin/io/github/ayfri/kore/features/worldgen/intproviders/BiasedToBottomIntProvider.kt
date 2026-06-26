package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Int provider that generates a random integer between [minInclusive] and [maxInclusive],
 * with results weighted towards the minimum value.
 */
@Serializable
@SerialName("minecraft:biased_to_bottom")
data class BiasedToBottomIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
) : IntProvider
