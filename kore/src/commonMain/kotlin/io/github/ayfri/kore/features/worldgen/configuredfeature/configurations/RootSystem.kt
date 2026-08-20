package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `root_system` feature, growing a tree's root system alongside [feature].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#root_system
 *
 * @property levelTestDistance The distance, in chunks, over which the surrounding terrain level is validated.
 * @property maxLevelDeviation The maximum terrain level deviation tolerated within [levelTestDistance].
 */
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
	var rootStateProvider: BlockStateProvider = SimpleStateProvider(),
	var hangingRootStateProvider: BlockStateProvider = SimpleStateProvider(),
	var allowedTreePosition: BlockPredicate = True,
	var levelTestDistance: Int = 0,
	var maxLevelDeviation: Int = 0,
	var feature: PlacedFeatureArgument,
) : FeatureConfig(), BlockPredicateScope, BlockStateProviderScope

fun ConfiguredFeatures.rootSystem(
	fileName: String,
	feature: PlacedFeatureArgument,
	block: RootSystem.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, RootSystem(feature = feature).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

/**
 * Sets [RootSystem.allowedTreePosition] to the predicate built in [block], the positions the tree may be placed at.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * rootSystem("azalea_root_system", feature = PlacedFeatures.AZALEA_TREE) {
 *     allowedTreePosition { solid { offset(0, -1, 0) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun RootSystem.allowedTreePosition(block: BlockPredicatesScope.() -> Unit) {
	allowedTreePosition = blockPredicate(block)
}
