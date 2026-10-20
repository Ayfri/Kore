package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.TreeDecorator
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import kotlinx.serialization.Serializable

/**
 * Configuration for the `fallen_tree` feature.
 *
 * Places a tree trunk lying on the ground, ending in a short stump. Vanilla uses it for the fallen oak, birch, super
 * birch, jungle, spruce and poplar trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#fallen_tree
 *
 * @property logDecorators The decorators run on the lying log, such as the mushrooms growing on top of it.
 * @property logLength Number of blocks in the lying log, `0` to `16`.
 * @property stumpDecorators The decorators run on the stump, such as the vines hanging on it.
 * @property trunkProvider The block state provider of the log and the stump, which needs an `axis` property.
 */
@Serializable
data class FallenTree(
	var logDecorators: List<TreeDecorator> = emptyList(),
	var logLength: IntProvider = ConstantIntProvider(0),
	var stumpDecorators: List<TreeDecorator> = emptyList(),
	var trunkProvider: BlockStateProvider = SimpleStateProvider(),
) : FeatureConfig(), BlockStateProviderScope, IntProviderScope

/**
 * Creates a `fallen_tree` configured feature, configured in [block].
 *
 * The block state provider and int provider builders are scoped to [block].
 *
 * ```kotlin
 * fallenTree("fallen_oak_tree") {
 *     trunkProvider = simpleStateProvider(blockState(Blocks.OAK_LOG, "axis" to "y"))
 *     logLength = uniform(4, 7)
 *     stumpDecorators { trunkVine() }
 *     logDecorators {
 *         attachedToLogs(Direction.UP) {
 *             probability = 0.1
 *             blockProvider = simpleStateProvider(Blocks.RED_MUSHROOM)
 *         }
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#fallen_tree
 */
fun ConfiguredFeatures.fallenTree(fileName: String, block: FallenTree.() -> Unit = {}) =
	dp.configuredFeature(fileName, FallenTree().apply(block))

/**
 * Sets [FallenTree.logDecorators] to the decorators appended in [block].
 *
 * ```kotlin
 * fallenTree("fallen_birch_tree") {
 *     logDecorators {
 *         attachedToLogs(Direction.UP) {
 *             probability = 0.1
 *             blockProvider = simpleStateProvider(Blocks.BROWN_MUSHROOM)
 *         }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#fallen_tree
 */
fun FallenTree.logDecorators(block: MutableList<TreeDecorator>.() -> Unit) {
	logDecorators = buildList(block)
}

/**
 * Sets [FallenTree.stumpDecorators] to the decorators appended in [block].
 *
 * ```kotlin
 * fallenTree("fallen_jungle_tree") {
 *     stumpDecorators { trunkVine() }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#fallen_tree
 */
fun FallenTree.stumpDecorators(block: MutableList<TreeDecorator>.() -> Unit) {
	stumpDecorators = buildList(block)
}
