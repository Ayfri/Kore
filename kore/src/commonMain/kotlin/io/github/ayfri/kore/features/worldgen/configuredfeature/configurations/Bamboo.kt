package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

@Serializable
data class Bamboo(
	var probability: Double = 0.0,
) : FeatureConfig()

fun ConfiguredFeatures.bamboo(fileName: String, probability: Double) =
	dp.configuredFeature(fileName, Bamboo(probability))
