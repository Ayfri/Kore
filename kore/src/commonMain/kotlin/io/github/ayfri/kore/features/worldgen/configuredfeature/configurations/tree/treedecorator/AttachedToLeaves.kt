package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Attaches blocks to the exposed faces of the leaves in the given directions, such as the hanging propagules of the
 * mangrove trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property probability Chance of attaching a block to a leaf, `0.0` to `1.0`, `0.14` for vanilla mangroves.
 * @property exclusionRadiusXZ Minimum horizontal distance between two attached blocks, `0` to `16`, `1` for vanilla
 * mangroves. Serialized as `exclusion_radius_xz`.
 * @property exclusionRadiusY Minimum vertical distance between two attached blocks, `0` to `16`, `0` for vanilla mangroves.
 * @property requiredEmptyBlocks Number of empty blocks needed in the attachment direction, `1` to `16`, `2` for vanilla
 * mangroves.
 * @property blockProvider The block states attached to the leaves.
 * @property directions The directions blocks are attached in, at least one.
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
 * Creates an [AttachedToLeaves] configured in [block].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * val propagules = attachedToLeaves {
 *     probability = 0.14
 *     exclusionRadiusXZ = 1
 *     requiredEmptyBlocks = 2
 *     blockProvider = simpleStateProvider(Blocks.MANGROVE_PROPAGULE)
 *     directions(Direction.DOWN)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun attachedToLeaves(block: AttachedToLeaves.() -> Unit = {}) = AttachedToLeaves().apply(block)

/**
 * Appends an [AttachedToLeaves] configured in [block].
 *
 * ```kotlin
 * tree("mangrove") {
 *     decorators {
 *         attachedToLeaves {
 *             probability = 0.14
 *             blockProvider = simpleStateProvider(Blocks.MANGROVE_PROPAGULE)
 *             directions(Direction.DOWN)
 *         }
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun MutableList<TreeDecorator>.attachedToLeaves(block: AttachedToLeaves.() -> Unit = {}) {
	this += AttachedToLeaves().apply(block)
}

/** Sets [AttachedToLeaves.directions] to [directions], the directions a block may be attached in. */
fun AttachedToLeaves.directions(vararg directions: Direction) {
	this.directions = directions.toList()
}
