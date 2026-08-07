package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class RootSystem(
	var requiredVerticalSpaceForTree: Int = 0,
	var rootRadius: Int = 0,
	var rootReplaceable: InlinableList<BlockOrTagArgument> = emptyList(),
	var rootPlacementAttempts: Int = 0,
	var rootColumnMaxHeight: Int = 0,
	var hangingRootRadius: Int = 0,
	var hangingRootsVerticalSpan: Int = 0,
	var hangingRootPlacementAttempts: Int = 0,
	var allowedVerticalWaterForTree: Int = 0,
	var rootStateProvider: BlockStateProvider = simpleStateProvider(),
	var hangingRootStateProvider: BlockStateProvider = simpleStateProvider(),
	var allowedTreePosition: BlockPredicate = True,
	var feature: PlacedFeatureArgument,
) : FeatureConfig()

fun ConfiguredFeatures.rootSystem(
	fileName: String,
	feature: PlacedFeatureArgument,
	block: RootSystem.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, RootSystem(feature = feature).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
