package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Int provider that clamps the output of [source] so it is never below [minInclusive] or above [maxInclusive].
 */
@Serializable
@SerialName("minecraft:clamped")
data class ClampedIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
	var source: IntProvider
) : IntProvider
