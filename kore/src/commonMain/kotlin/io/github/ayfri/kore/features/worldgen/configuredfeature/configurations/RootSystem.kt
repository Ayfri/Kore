package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `root_system` feature.
 *
 * Looks up from the placement position for a valid spot, runs [feature] there, then grows a column of roots and hanging
 * roots in the ground below. Vanilla grows the rooted azalea trees of the lush caves with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#root_system
 *
 * @property requiredVerticalSpaceForTree Number of air or water blocks needed above a spot, `1` to `64`.
 * @property rootRadius Horizontal radius the roots are placed in, `1` to `64`.
 * @property rootReplaceable A block, a block tag or a list of blocks the roots can replace.
 * @property rootPlacementAttempts Number of attempts to place a root, `1` to `256`.
 * @property rootColumnMaxHeight How far up from the placement position a spot is searched, `1` to `4096`.
 * @property hangingRootRadius Horizontal radius the hanging roots are placed in, `1` to `64`.
 * @property hangingRootsVerticalSpan Vertical range the hanging roots are placed in, `1` to `16`.
 * @property hangingRootPlacementAttempts Number of attempts to place a hanging root, `0` to `256`.
 * @property allowedVerticalWaterForTree Maximum height of the water above a spot, `1` to `64`.
 * @property rootStateProvider The block state provider of the roots.
 * @property hangingRootStateProvider The block state provider of the hanging roots.
 * @property allowedTreePosition The spots the tree can be placed at, every position by default.
 * @property levelTestDistance Distance, `0` to `16`, at which the terrain around a spot must be level, `0` skips the
 * check.
 * @property maxLevelDeviation Maximum height difference tolerated by the level check, `0` to `64`, with the check on
 * `0` always fails it.
 * @property feature The placed feature run on top of the root system.
 */
@Serializable
data class RootSystem(
	var requiredVerticalSpaceForTree: Int = 1,
	var rootRadius: Int = 1,
	var rootReplaceable: InlinableList<BlockOrTagArgument> = emptyList(),
	var rootPlacementAttempts: Int = 1,
	var rootColumnMaxHeight: Int = 1,
	var hangingRootRadius: Int = 1,
	var hangingRootsVerticalSpan: Int = 1,
	var hangingRootPlacementAttempts: Int = 0,
	var allowedVerticalWaterForTree: Int = 1,
	var rootStateProvider: BlockStateProvider = SimpleStateProvider(),
	var hangingRootStateProvider: BlockStateProvider = SimpleStateProvider(),
	var allowedTreePosition: BlockPredicate = True,
	var levelTestDistance: Int = 0,
	var maxLevelDeviation: Int = 0,
	var feature: PlacedFeatureArgument,
) : FeatureConfig(), BlockPredicateScope, BlockStateProviderScope

/**
 * Creates a `root_system` configured feature running [feature] above its roots, configured in [block].
 *
 * The block state provider and block predicate builders are scoped to [block].
 *
 * ```kotlin
 * rootSystem("oak_roots", feature = PlacedFeatures.OAK_CHECKED) {
 *     requiredVerticalSpaceForTree = 3
 *     rootRadius = 3
 *     rootReplaceable = listOf(Tags.Block.AZALEA_ROOT_REPLACEABLE)
 *     rootPlacementAttempts = 20
 *     rootColumnMaxHeight = 100
 *     hangingRootRadius = 3
 *     hangingRootsVerticalSpan = 2
 *     hangingRootPlacementAttempts = 20
 *     allowedVerticalWaterForTree = 2
 *     rootStateProvider = simpleStateProvider(Blocks.ROOTED_DIRT)
 *     hangingRootStateProvider = simpleStateProvider(Blocks.HANGING_ROOTS)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#root_system
 */
fun ConfiguredFeatures.rootSystem(
	fileName: String,
	feature: PlacedFeatureArgument,
	block: RootSystem.() -> Unit = {},
) = dp.configuredFeature(fileName, RootSystem(feature = feature).apply(block))

/**
 * Sets [RootSystem.allowedTreePosition] to the predicate built in [block], the positions the tree may be placed at.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * rootSystem("oak_roots", feature = PlacedFeatures.OAK_CHECKED) {
 *     allowedTreePosition { solid { offset(0, -1, 0) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun RootSystem.allowedTreePosition(block: BlockPredicatesScope.() -> Unit) {
	allowedTreePosition = blockPredicate(block)
}
