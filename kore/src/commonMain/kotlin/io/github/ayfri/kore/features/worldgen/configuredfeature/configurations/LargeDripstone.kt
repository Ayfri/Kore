package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.floatproviders.ConstantFloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class LargeDripstone(
	var floorToCeilingSearchRange: Int? = null,
	var columnRadius: IntProvider = ConstantIntProvider(0),
	var heightScale: FloatProvider = ConstantFloatProvider(0f),
	var maxColumnRadiusToCaveHeightRatio: Float = 0f,
	var stalactiteBluntness: FloatProvider = ConstantFloatProvider(0f),
	var stalagmiteBluntness: FloatProvider = ConstantFloatProvider(0f),
	var windSpeed: FloatProvider = ConstantFloatProvider(0f),
	var minRadiusForWind: Int = 0,
	var minBluntnessForWind: Float = 0f,
	var replaceableBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig(), FloatProviderScope, IntProviderScope

fun ConfiguredFeatures.largeDripstone(fileName: String, block: LargeDripstone.() -> Unit = {}): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, LargeDripstone().apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
