package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

/**
 * Places shelf mushrooms on the logs of the tree.
 *
 * @property probability The chance to place any shelf mushroom on the tree, between `0.0` and `1.0`.
 */
@Serializable
data class ShelfMushroom(
	var probability: Double = 0.0,
) : TreeDecorator()

fun shelfMushroom(probability: Double = 0.0) = ShelfMushroom(probability)

fun MutableList<TreeDecorator>.shelfMushroom(probability: Double = 0.0) {
	this += ShelfMushroom(probability)
}
