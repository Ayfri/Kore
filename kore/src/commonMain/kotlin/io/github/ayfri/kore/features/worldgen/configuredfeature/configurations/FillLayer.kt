package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.FeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class FillLayer(
	var state: BlockState = blockStateStone(),
	var height: Int = 0,
) : FeatureConfig()

fun ConfiguredFeatures.fillLayer(fileName: String, state: BlockState = blockStateStone(), height: Int = 0): FeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, FillLayer(state, height))
	dp.configuredFeatures += configuredFeature
	return FeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
