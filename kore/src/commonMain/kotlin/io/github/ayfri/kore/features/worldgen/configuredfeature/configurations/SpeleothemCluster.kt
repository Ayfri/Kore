package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class SpeleothemCluster(
	var floorToCeilingSearchRange: Int = 0,
	var height: IntProvider = constant(0),
	var radius: IntProvider = constant(0),
	var maxStalagmiteStalactiteHeightDiff: Int = 0,
	var heightDeviation: Int = 0,
	var speleothemBlockLayerThickness: IntProvider = constant(0),
	var density: IntProvider = constant(0),
	var wetness: IntProvider = constant(0),
	var chanceOfSpeleothemAtMaxDistanceFromCenter: Int = 0,
	var maxDistanceFromEdgeAffectingChanceOfSpeleothem: Int = 0,
	var maxDistanceFromCenterAffectingHeightBias: Int = 0,
	var baseBlock: BlockState = blockStateStone(),
	var pointedBlock: BlockState = blockStateStone(),
	var replaceableBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig()

fun speleothemCluster(
	block: SpeleothemCluster.() -> Unit = {},
) = SpeleothemCluster().apply(block)
