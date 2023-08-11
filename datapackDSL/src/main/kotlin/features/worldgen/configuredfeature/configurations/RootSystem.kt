package features.worldgen.configuredfeature.configurations

import arguments.types.resources.worldgen.PlacedFeatureArgument
import features.worldgen.configuredfeature.blockpredicate.BlockPredicate
import features.worldgen.configuredfeature.blockpredicate.True
import features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class RootSystem(
	var requiredVerticalSpaceForTree: Int = 0,
	var rootRadius: Int = 0,
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

fun rootSystem(
	feature: PlacedFeatureArgument,
	block: RootSystem.() -> Unit = {},
) = RootSystem(feature = feature).apply(block)
