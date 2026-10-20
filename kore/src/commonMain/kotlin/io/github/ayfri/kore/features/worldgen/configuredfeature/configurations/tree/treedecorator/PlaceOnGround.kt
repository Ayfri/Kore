package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import kotlinx.serialization.Serializable

/**
 * Scatters blocks on the ground around the tree. Vanilla uses it for the leaf litter of the `*_leaf_litter` trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property blockStateProvider The block states placed on the ground.
 * @property height How far above and below the tree base the ground is searched, at least `0`, `1` when `null`.
 * @property radius Horizontal distance from the trunk the blocks are placed within, at least `0`, `2` when `null`.
 * @property tries Number of placement attempts, at least `1`, `128` when `null`.
 */
@Serializable
data class PlaceOnGround(
	var blockStateProvider: BlockStateProvider = SimpleStateProvider(),
	var height: Int? = null,
	var radius: Int? = null,
	var tries: Int? = null,
) : TreeDecorator(), BlockStateProviderScope

/**
 * Creates a [PlaceOnGround] configured in [block].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * tree("oak_leaf_litter") {
 *     decorators(
 *         placeOnGround {
 *             blockStateProvider = simpleStateProvider(blockState(Blocks.LEAF_LITTER, "segment_amount" to "1"))
 *             radius = 4
 *             tries = 96
 *         }
 *     )
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun placeOnGround(block: PlaceOnGround.() -> Unit = {}) = PlaceOnGround().apply(block)

/**
 * Appends a [PlaceOnGround] configured in [block].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * tree("oak_leaf_litter") {
 *     decorators {
 *         placeOnGround {
 *             blockStateProvider = simpleStateProvider(blockState(Blocks.LEAF_LITTER, "segment_amount" to "1"))
 *             height = 2
 *             tries = 150
 *         }
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun MutableList<TreeDecorator>.placeOnGround(block: PlaceOnGround.() -> Unit = {}) {
	this += PlaceOnGround().apply(block)
}
