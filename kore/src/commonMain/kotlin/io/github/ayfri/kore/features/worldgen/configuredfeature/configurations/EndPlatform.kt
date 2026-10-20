package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

@Serializable
data object EndPlatform : FeatureConfig()

fun ConfiguredFeatures.endPlatform(fileName: String) = dp.configuredFeature(fileName, EndPlatform)
