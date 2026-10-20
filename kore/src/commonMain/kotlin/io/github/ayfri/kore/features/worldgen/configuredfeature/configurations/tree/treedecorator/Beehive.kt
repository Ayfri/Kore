package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

/**
 * Places a single beehive on the tree, one block below its lowest leaves. Vanilla uses it for the `*_bees_*` variants of
 * the oak, birch, large oak and cherry trees, and for the mangrove trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property probability Chance of placing the beehive, `0.0` to `1.0`, from `0.002` to `1.0` for the vanilla bee trees and
 * `0.01` for mangroves.
 */
@Serializable
data class Beehive(
	var probability: Double = 0.0,
) : TreeDecorator()

/**
 * Creates a [Beehive] with the given [probability].
 *
 * ```kotlin
 * tree("oak_bees") {
 *     decorators(beehive(0.05))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun beehive(probability: Double = 0.0) = Beehive(probability)

/**
 * Appends a [Beehive] with the given [probability].
 *
 * ```kotlin
 * tree("oak_bees") {
 *     decorators { beehive(0.05) }
 * }
 * ```
 */
fun MutableList<TreeDecorator>.beehive(probability: Double = 0.0) {
	this += Beehive(probability)
}
