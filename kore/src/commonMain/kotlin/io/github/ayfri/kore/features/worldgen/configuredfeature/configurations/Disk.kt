package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Disk(
	var stateProvider: BlockStateProvider,
	var target: BlockPredicate,
	var radius: IntProvider = constant(0),
	var halfHeight: Int = 0,
) : FeatureConfig()

fun ConfiguredFeatures.disk(
	fileName: String,
	stateProvider: BlockStateProvider,
	target: BlockPredicate,
	radius: IntProvider = constant(0),
	halfHeight: Int = 0,
	block: Disk.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Disk(stateProvider, target, radius, halfHeight).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

fun ConfiguredFeatures.disk(
	fileName: String,
	stateProvider: BlockStateProvider,
	target: BlockPredicate,
	radius: Int,
	halfHeight: Int = 0,
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Disk(stateProvider, target, constant(radius), halfHeight))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
