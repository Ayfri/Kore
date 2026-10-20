package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

@Serializable
data class Seagrass(
	var probability: Double = 0.0,
) : FeatureConfig()

fun ConfiguredFeatures.seagrass(fileName: String, probability: Double = 0.0) =
	dp.configuredFeature(fileName, Seagrass(probability))
