package features.worldgen.configuredfeature.configurations

import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
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

fun sculkPatch(
	block: SculkPatch.() -> Unit = {},
) = SculkPatch().apply(block)
