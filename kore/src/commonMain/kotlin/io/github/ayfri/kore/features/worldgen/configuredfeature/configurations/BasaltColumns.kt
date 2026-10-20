package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class BasaltColumns(
	var reach: IntProvider = ConstantIntProvider(0),
	var height: IntProvider = ConstantIntProvider(0),
) : FeatureConfig(), IntProviderScope

fun ConfiguredFeatures.basaltColumns(
	fileName: String,
	reach: IntProvider = ConstantIntProvider(0),
	height: IntProvider = ConstantIntProvider(0),
) = dp.configuredFeature(fileName, BasaltColumns(reach, height))

fun ConfiguredFeatures.basaltColumns(fileName: String, reach: Int, height: Int) =
	dp.configuredFeature(fileName, BasaltColumns(constant(reach), constant(height)))
