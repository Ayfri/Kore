package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import kotlinx.serialization.Serializable

@Serializable
data class Seagrass(
	var probability: Double = 0.0,
) : FeatureConfig()

fun seagrass(probability: Double = 0.0) = Seagrass(probability)
