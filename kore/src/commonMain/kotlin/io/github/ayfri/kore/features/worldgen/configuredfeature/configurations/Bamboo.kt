package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import kotlinx.serialization.Serializable

@Serializable
data class Bamboo(
	var probability: Double = 0.0,
) : FeatureConfig()

fun bamboo(probability: Double) = Bamboo(probability)
