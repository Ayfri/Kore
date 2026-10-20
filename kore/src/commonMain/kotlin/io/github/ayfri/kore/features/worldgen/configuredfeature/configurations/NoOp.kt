package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `no_op` feature, which has no field.
 *
 * Places nothing, so it can replace an existing configured feature to turn it off. Vanilla doesn't use it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#no_op
 */
@Serializable
data object NoOp : FeatureConfig()

/** Creates a `no_op` configured feature, which has no configuration. */
fun ConfiguredFeatures.noOp(fileName: String) = dp.configuredFeature(fileName, NoOp)
