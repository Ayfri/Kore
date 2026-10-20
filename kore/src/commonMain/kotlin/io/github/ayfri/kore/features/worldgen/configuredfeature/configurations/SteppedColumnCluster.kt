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
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `stepped_column_cluster` feature.
 *
 * Grows a cluster of columns up from the ground, each one stepping down towards its edges. Vanilla builds the basalt
 * columns of the basalt deltas with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#stepped_column_cluster
 *
 * @property block The block state provider of the columns.
 * @property canReplace The blocks a column can grow into.
 * @property cannotPlaceOn A block, a block tag or a list of blocks a column cannot start from.
 * @property clusterReach Horizontal spread of the cluster around the placement position, `0` to `13`, limited by [height].
 * @property columnCount Number of columns in the cluster, `1` to `150`.
 * @property columnReach Horizontal reach of every column, `0` to `3`.
 * @property continueThrough The blocks a growing column goes through instead of stopping, such as the columns' own block.
 * @property height Height of the columns, `1` to `10`.
 */
@Serializable
data class SteppedColumnCluster(
	var block: BlockStateProvider = SimpleStateProvider(),
	var canReplace: BlockPredicate = True,
	var cannotPlaceOn: InlinableList<BlockOrTagArgument> = emptyList(),
	var clusterReach: IntProvider = ConstantIntProvider(0),
	var columnCount: IntProvider = ConstantIntProvider(1),
	var columnReach: IntProvider = ConstantIntProvider(0),
	var continueThrough: BlockPredicate = True,
	var height: IntProvider = ConstantIntProvider(1),
) : FeatureConfig(), BlockPredicateScope, BlockStateProviderScope, IntProviderScope

/**
 * Creates a `stepped_column_cluster` configured feature, configured in [block].
 *
 * The block state provider, int provider and block predicate builders are scoped to [block].
 *
 * ```kotlin
 * steppedColumnCluster("basalt_cluster") {
 *     block = simpleStateProvider(Blocks.BASALT)
 *     canReplace { matchingBlocks(Blocks.AIR, Blocks.LAVA) }
 *     continueThrough { matchingBlocks(Blocks.BASALT) }
 *     cannotPlaceOn(Blocks.LAVA)
 *     columnCount = constant(50)
 *     columnReach = uniform(2, 3)
 *     clusterReach = constant(5)
 *     height = uniform(5, 10)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#stepped_column_cluster
 */
fun ConfiguredFeatures.steppedColumnCluster(fileName: String, block: SteppedColumnCluster.() -> Unit = {}) =
	dp.configuredFeature(fileName, SteppedColumnCluster().apply(block))

/**
 * Sets [SteppedColumnCluster.canReplace] to the predicate built in [block], the blocks the columns can grow into.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * steppedColumnCluster("basalt_cluster") {
 *     canReplace { matchingBlocks(Blocks.AIR, Blocks.LAVA) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun SteppedColumnCluster.canReplace(block: BlockPredicatesScope.() -> Unit) {
	canReplace = blockPredicate(block)
}

/**
 * Sets [SteppedColumnCluster.cannotPlaceOn] to [blocks], the blocks (or block tags) the columns cannot start from.
 *
 * ```kotlin
 * steppedColumnCluster("basalt_cluster") {
 *     cannotPlaceOn(Blocks.LAVA, Blocks.MAGMA_BLOCK)
 * }
 * ```
 */
fun SteppedColumnCluster.cannotPlaceOn(vararg blocks: BlockOrTagArgument) {
	cannotPlaceOn = blocks.toList()
}

/**
 * Sets [SteppedColumnCluster.continueThrough] to the predicate built in [block], the blocks a growing column goes
 * through instead of stopping.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * steppedColumnCluster("basalt_cluster") {
 *     continueThrough { matchingBlocks(Blocks.BASALT) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun SteppedColumnCluster.continueThrough(block: BlockPredicatesScope.() -> Unit) {
	continueThrough = blockPredicate(block)
}
