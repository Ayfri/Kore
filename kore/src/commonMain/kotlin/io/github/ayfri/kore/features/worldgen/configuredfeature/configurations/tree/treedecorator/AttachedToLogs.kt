package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleBlockStateProvider
import kotlinx.serialization.Serializable

/**
 * Attaches blocks to the exposed faces of the logs in the given directions, such as the mushrooms growing on top of
 * the fallen trees. Vanilla uses it for the `fallen_*_tree` features.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property blockProvider The block states attached to the logs.
 * @property directions The directions blocks are attached in, required and never empty, `up` for the vanilla fallen trees.
 * @property probability Chance of attaching a block to a log, `0.0` to `1.0`, `0.1` for the vanilla fallen trees.
 */
@Serializable
data class AttachedToLogs(
	var blockProvider: BlockStateProvider = SimpleBlockStateProvider(),
	var directions: List<Direction>,
	var probability: Double = 0.0,
) : TreeDecorator(), BlockStateProviderScope

/**
 * Creates an [AttachedToLogs] attaching blocks in [directions], at least one, configured in [block].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * val mushrooms = attachedToLogs(Direction.UP) {
 *     probability = 0.1
 *     blockProvider = simpleStateProvider(Blocks.RED_MUSHROOM)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun attachedToLogs(vararg directions: Direction, block: AttachedToLogs.() -> Unit = {}) =
	AttachedToLogs(directions = directions.toList()).apply(block)

/**
 * Appends an [AttachedToLogs] attaching blocks in [directions], at least one, configured in [block].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * tree("mossy_oak") {
 *     decorators {
 *         attachedToLogs(Direction.UP) {
 *             probability = 0.1
 *             blockProvider = simpleStateProvider(Blocks.RED_MUSHROOM)
 *         }
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun MutableList<TreeDecorator>.attachedToLogs(vararg directions: Direction, block: AttachedToLogs.() -> Unit = {}) {
	this += AttachedToLogs(directions = directions.toList()).apply(block)
}
