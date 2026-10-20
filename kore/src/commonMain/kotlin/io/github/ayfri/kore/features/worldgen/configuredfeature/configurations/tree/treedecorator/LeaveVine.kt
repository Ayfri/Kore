package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

/**
 * Hangs vines from the leaves of the tree. Vanilla uses it for the jungle, mega jungle, swamp oak and mangrove trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property probability Chance of placing a vine at a given leaf position, `0.0` to `1.0`, `0.25` for vanilla jungle trees
 * and `0.125` for mangroves.
 */
@Serializable
data class LeaveVine(
	var probability: Double = 0.0,
) : TreeDecorator()

/**
 * Creates a [LeaveVine] with the given [probability].
 *
 * ```kotlin
 * tree("jungle") {
 *     decorators(leaveVine(0.25))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun leaveVine(probability: Double = 0.0) = LeaveVine(probability)

/**
 * Appends a [LeaveVine] with the given [probability].
 *
 * ```kotlin
 * tree("jungle") {
 *     decorators { leaveVine(0.25) }
 * }
 * ```
 */
fun MutableList<TreeDecorator>.leaveVine(probability: Double = 0.0) {
	this += LeaveVine(probability)
}
