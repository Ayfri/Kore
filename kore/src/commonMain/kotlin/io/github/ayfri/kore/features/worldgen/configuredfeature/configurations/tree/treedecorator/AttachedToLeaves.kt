package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Attaches blocks to the leaves of a placed tree, such as the cocoa beans of the jungle trees.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#attached_to_leaves
 *
 * @property probability The chance of attaching a block to a given leaf block, between `0.0` and `1.0`.
 * @property exclusionRadiusXZ The horizontal distance kept between two attached blocks.
 * @property exclusionRadiusY The vertical distance kept between two attached blocks.
 * @property requiredEmptyBlocks The amount of empty blocks needed in the chosen direction.
 * @property blockProvider The block states attached to the leaves.
 * @property directions The directions a block may be attached in.
 */
@Serializable
data class AttachedToLeaves(
	var probability: Double = 0.0,
	@SerialName("exclusion_radius_xz")
	var exclusionRadiusXZ: Int = 0,
	var exclusionRadiusY: Int = 0,
	var requiredEmptyBlocks: Int = 0,
	var blockProvider: BlockStateProvider = SimpleStateProvider(),
	var directions: List<Direction> = emptyList(),
) : TreeDecorator(), BlockStateProviderScope

/**
 * Creates an `attached_to_leaves` tree decorator.
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * attachedToLeaves {
 *     probability = 0.14
 *     exclusionRadiusXZ = 1
 *     requiredEmptyBlocks = 2
 *     blockProvider = simpleStateProvider(Blocks.COCOA)
 *     directions(Direction.NORTH, Direction.SOUTH)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#attached_to_leaves
 */
fun attachedToLeaves(block: AttachedToLeaves.() -> Unit = {}) = AttachedToLeaves().apply(block)

/**
 * Appends an `attached_to_leaves` tree decorator configured in [block].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#attached_to_leaves
 */
fun MutableList<TreeDecorator>.attachedToLeaves(block: AttachedToLeaves.() -> Unit = {}) {
	this += AttachedToLeaves().apply(block)
}

/** Sets [AttachedToLeaves.directions] to [directions], the directions a block may be attached in. */
fun AttachedToLeaves.directions(vararg directions: Direction) {
	this.directions = directions.toList()
}
