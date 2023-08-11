package features.worldgen.configuredfeature.configurations

import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class DripstoneCluster(
	var floorToCeilingSearchRange: Int = 0,
	var height: IntProvider = constant(0),
	var radius: IntProvider = constant(0),
	var maxStalagmiteStalactiteHeightDiff: Int = 0,
	var heightDeviation: Int = 0,
	var dripstoneBlockLayerThickness: IntProvider = constant(0),
	var density: IntProvider = constant(0),
	var wetness: IntProvider = constant(0),
	var chanceOfDripstoneColumnAtMaxDistanceFromCenter: Int = 0,
	var maxDistanceFromEdgeAffectingChanceOfDripstoneColumn: Int = 0,
	var maxDistanceFromCenterAffectingHeightBias: Int = 0,
) : FeatureConfig()

fun dripstoneCluster(
	block: DripstoneCluster.() -> Unit = {},
) = DripstoneCluster().apply(block)
