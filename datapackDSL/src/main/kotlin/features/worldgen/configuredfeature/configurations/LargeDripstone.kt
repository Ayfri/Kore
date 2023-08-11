package features.worldgen.configuredfeature.configurations

import features.worldgen.floatproviders.FloatProvider
import features.worldgen.floatproviders.constant
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class LargeDripstone(
	var floorToCeilingSearchRange: Int? = null,
	var columnRadius: IntProvider = constant(0),
	var heightScale: FloatProvider = constant(0f),
	var maxColumnRadiusToCaveHeightRatio: Float = 0f,
	var stalactiteBluntness: FloatProvider = constant(0f),
	var stalagmiteBluntness: FloatProvider = constant(0f),
	var windSpeed: FloatProvider = constant(0f),
	var minRadiusForWind: Int = 0,
	var minBluntnessForWind: Float = 0f,
) : FeatureConfig()

fun largeDripstone(block: LargeDripstone.() -> Unit = {}) = LargeDripstone().apply(block)
