package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

/**
 * Adds vines to the sides of the trunk blocks. Vanilla uses it for the jungle and mega jungle trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
@Serializable
data object TrunkVine : TreeDecorator()

/**
 * Appends a [TrunkVine].
 *
 * ```kotlin
 * tree("jungle") {
 *     decorators { trunkVine() }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun MutableList<TreeDecorator>.trunkVine() {
	this += TrunkVine
}
