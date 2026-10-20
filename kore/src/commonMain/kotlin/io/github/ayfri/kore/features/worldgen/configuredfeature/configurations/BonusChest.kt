package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

@Serializable
data object BonusChest : FeatureConfig()

fun ConfiguredFeatures.bonusChest(fileName: String) = dp.configuredFeature(fileName, BonusChest)
