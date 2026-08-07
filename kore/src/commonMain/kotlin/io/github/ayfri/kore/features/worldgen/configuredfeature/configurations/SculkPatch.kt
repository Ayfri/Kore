package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class SculkPatch(
	var chargeCount: Int = 0,
	var amountPerCharge: Int = 0,
	var spreadAttempts: Int = 0,
	var growthRounds: Int = 0,
	var spreadRounds: Int = 0,
	var extraRateGrowths: IntProvider = constant(0),
	var catalystChance: Double = 0.0,
) : FeatureConfig()

fun ConfiguredFeatures.sculkPatch(fileName: String, block: SculkPatch.() -> Unit = {}): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, SculkPatch().apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
