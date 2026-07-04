package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class SeaPickle(
	var count: IntProvider = constant(0),
) : FeatureConfig()

fun seaPickle(count: IntProvider = constant(0)) = SeaPickle(count)
