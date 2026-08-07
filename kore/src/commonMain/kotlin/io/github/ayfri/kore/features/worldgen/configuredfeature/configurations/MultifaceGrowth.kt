package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class MultifaceGrowth(
	var block: BlockArgument? = null,
	var searchRange: Int? = null,
	var chanceOfSpreading: Double? = null,
	var canPlaceOnFloor: Boolean? = null,
	var canPlaceOnCeiling: Boolean? = null,
	var canPlaceOnWall: Boolean? = null,
	var canBePlacedOn: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig()

fun ConfiguredFeatures.multifaceGrowth(fileName: String, block: MultifaceGrowth.() -> Unit = {}): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, MultifaceGrowth().apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
