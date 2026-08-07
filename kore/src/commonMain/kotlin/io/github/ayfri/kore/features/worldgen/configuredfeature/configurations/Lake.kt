package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Lake(
	var fluid: BlockState = blockStateStone(),
	var barrier: BlockState = blockStateStone(),
	var canPlaceFeature: BlockPredicate = True,
	var canReplaceWithAirOrFluid: BlockPredicate = True,
	var canReplaceWithBarrier: BlockPredicate = True,
) : FeatureConfig()

fun ConfiguredFeatures.lake(
	fileName: String,
	fluid: BlockState = blockStateStone(),
	barrier: BlockState = blockStateStone(),
	canPlaceFeature: BlockPredicate = True,
	canReplaceWithAirOrFluid: BlockPredicate = True,
	canReplaceWithBarrier: BlockPredicate = True,
	block: Lake.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(
		fileName,
		Lake(fluid, barrier, canPlaceFeature, canReplaceWithAirOrFluid, canReplaceWithBarrier).apply(block),
	)
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
