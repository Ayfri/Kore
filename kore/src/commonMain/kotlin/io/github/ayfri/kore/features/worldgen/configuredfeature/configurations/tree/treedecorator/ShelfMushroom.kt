package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

/**
 * Places shelf mushrooms on the logs of the tree, as on the poplar trees of 26.3.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property probability Chance of placing any shelf mushroom on the tree, `0.0` to `1.0`, `0.4` for vanilla poplars.
 */
@Serializable
data class ShelfMushroom(
	var probability: Double = 0.0,
) : TreeDecorator()

/**
 * Creates a [ShelfMushroom] with the given [probability].
 *
 * ```kotlin
 * tree("poplar") {
 *     decorators(shelfMushroom(0.4))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun shelfMushroom(probability: Double = 0.0) = ShelfMushroom(probability)

/**
 * Appends a [ShelfMushroom] with the given [probability].
 *
 * ```kotlin
 * tree("poplar") {
 *     decorators { shelfMushroom(0.4) }
 * }
 * ```
 */
fun MutableList<TreeDecorator>.shelfMushroom(probability: Double = 0.0) {
	this += ShelfMushroom(probability)
}
