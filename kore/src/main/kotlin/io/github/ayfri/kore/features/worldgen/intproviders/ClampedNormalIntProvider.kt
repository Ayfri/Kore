package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:clamped_normal")
data class ClampedNormalIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
	var mean: Float,
	var deviation: Float,
) : IntProvider
