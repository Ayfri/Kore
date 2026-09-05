package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class SpeleothemCluster(
	var floorToCeilingSearchRange: Int = 0,
	var height: IntProvider = ConstantIntProvider(0),
	var radius: IntProvider = ConstantIntProvider(0),
	var maxStalagmiteStalactiteHeightDiff: Int = 0,
	var heightDeviation: Int = 0,
	var speleothemBlockLayerThickness: IntProvider = ConstantIntProvider(0),
	var density: IntProvider = ConstantIntProvider(0),
	var wetness: IntProvider = ConstantIntProvider(0),
	var chanceOfSpeleothemAtMaxDistanceFromCenter: Int = 0,
	var maxDistanceFromEdgeAffectingChanceOfSpeleothem: Int = 0,
	var maxDistanceFromCenterAffectingHeightBias: Int = 0,
	var baseBlock: BlockState = blockStateStone(),
	var pointedBlock: BlockState = blockStateStone(),
	var replaceableBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig(), IntProviderScope

fun ConfiguredFeatures.speleothemCluster(fileName: String, block: SpeleothemCluster.() -> Unit = {}): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, SpeleothemCluster().apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
