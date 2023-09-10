package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.constant
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
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
