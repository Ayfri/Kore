package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

/**
 * Places cocoa beans on the bottom blocks of the trunk, as on the jungle trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property probability Chance of placing any cocoa beans on the tree, `0.0` to `1.0`, `0.2` for vanilla jungle trees.
 */
@Serializable
data class Cocoa(
	var probability: Double = 0.0,
) : TreeDecorator()

/**
 * Creates a [Cocoa] with the given [probability].
 *
 * ```kotlin
 * tree("jungle") {
 *     decorators(cocoa(0.2))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun cocoa(probability: Double = 0.0) = Cocoa(probability)

/**
 * Appends a [Cocoa] with the given [probability].
 *
 * ```kotlin
 * tree("jungle") {
 *     decorators { cocoa(0.2) }
 * }
 * ```
 */
fun MutableList<TreeDecorator>.cocoa(probability: Double = 0.0) {
	this += Cocoa(probability)
}
