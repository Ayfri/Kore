package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

@Serializable
data class TwistingVines(
	var spreadWidth: Int = 0,
	var spreadHeight: Int = 0,
	var maxHeight: Int = 0,
) : FeatureConfig()

fun ConfiguredFeatures.twistingVines(
	fileName: String,
	spreadWidth: Int = 0,
	spreadHeight: Int = 0,
	maxHeight: Int = 0,
	block: TwistingVines.() -> Unit = {},
) = dp.configuredFeature(fileName, TwistingVines(spreadWidth, spreadHeight, maxHeight).apply(block))
