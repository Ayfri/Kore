package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `multiface_growth` feature.
 *
 * [block] is the multiface block that spreads over the surfaces allowed by [canPlaceOnFloor],
 * [canPlaceOnCeiling] and [canPlaceOnWall].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#multiface_growth
 */
@Serializable
data class MultifaceGrowth(
	var block: BlockArgument,
	var searchRange: Int? = null,
	var chanceOfSpreading: Double? = null,
	var canPlaceOnFloor: Boolean? = null,
	var canPlaceOnCeiling: Boolean? = null,
	var canPlaceOnWall: Boolean? = null,
	var canBePlacedOn: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig()

/** Creates a [MultifaceGrowth] configuration spreading [block] over the configured surfaces. */
fun ConfiguredFeatures.multifaceGrowth(
	fileName: String,
	block: BlockArgument,
	init: MultifaceGrowth.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, MultifaceGrowth(block).apply(init))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
