package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

@Serializable
data object CoralClaw : FeatureConfig()

fun ConfiguredFeatures.coralClaw(fileName: String) = dp.configuredFeature(fileName, CoralClaw)
