package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class DeltaFeature(
	var content: BlockState = blockStateStone(),
	var rim: BlockState = blockStateStone(),
	var size: IntProvider = constant(0),
	var rimSize: IntProvider = constant(0),
) : FeatureConfig()

fun ConfiguredFeatures.deltaFeature(
	fileName: String,
	content: BlockState = blockStateStone(),
	rim: BlockState = blockStateStone(),
	size: IntProvider = constant(0),
	rimSize: IntProvider = constant(0),
	block: DeltaFeature.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, DeltaFeature(content, rim, size, rimSize).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

fun ConfiguredFeatures.deltaFeature(
	fileName: String,
	content: BlockState = blockStateStone(),
	rim: BlockState = blockStateStone(),
	size: Int,
	rimSize: Int,
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, DeltaFeature(content, rim, constant(size), constant(rimSize)))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
