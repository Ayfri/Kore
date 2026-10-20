package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

@Serializable
data object DesertWell : FeatureConfig()

fun ConfiguredFeatures.desertWell(fileName: String) = dp.configuredFeature(fileName, DesertWell)
