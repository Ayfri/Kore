package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

@Serializable
data object Vines : FeatureConfig()

fun ConfiguredFeatures.vines(fileName: String) = dp.configuredFeature(fileName, Vines)
