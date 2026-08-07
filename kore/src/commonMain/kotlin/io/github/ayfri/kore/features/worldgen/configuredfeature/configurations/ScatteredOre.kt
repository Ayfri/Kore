package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.Target
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class ScatteredOre(
	var size: Int = 0,
	var discardChanceOnAirExposure: Double = 0.0,
	var targets: List<Target> = emptyList(),
) : FeatureConfig()

fun ConfiguredFeatures.scatteredOre(
	fileName: String,
	size: Int = 0,
	discardChanceOnAirExposure: Double = 0.0,
	targets: List<Target> = emptyList(),
	block: ScatteredOre.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, ScatteredOre(size, discardChanceOnAirExposure, targets).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

fun ConfiguredFeatures.scatteredOre(
	fileName: String,
	size: Int = 0,
	discardChanceOnAirExposure: Double = 0.0,
	vararg targets: Target,
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, ScatteredOre(size, discardChanceOnAirExposure, targets.toList()))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
