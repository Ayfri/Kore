package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.FeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Iceberg(
	var state: BlockState = blockStateStone(),
) : FeatureConfig()

fun ConfiguredFeatures.iceberg(fileName: String, state: BlockState = blockStateStone()): FeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Iceberg(state))
	dp.configuredFeatures += configuredFeature
	return FeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
