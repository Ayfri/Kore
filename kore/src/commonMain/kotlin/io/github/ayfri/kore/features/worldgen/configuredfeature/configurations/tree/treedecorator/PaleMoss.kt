package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

/**
 * Covers the tree and the ground around it with pale moss, pale moss carpets and hanging pale moss. Vanilla uses it for
 * the pale oak trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property groundProbability Chance of covering the ground around the tree, `0.0` to `1.0`, `0.8` for vanilla pale oaks.
 * @property leavesProbability Chance of hanging moss under a leaf, `0.0` to `1.0`, `0.15` for vanilla pale oaks.
 * @property trunkProbability Chance of covering the trunk, `0.0` to `1.0`, `0.4` for vanilla pale oaks.
 */
@Serializable
data class PaleMoss(
	var groundProbability: Double = 0.0,
	var leavesProbability: Double = 0.0,
	var trunkProbability: Double = 0.0,
) : TreeDecorator()

/**
 * Creates a [PaleMoss] with the given probabilities.
 *
 * ```kotlin
 * tree("pale_oak") {
 *     decorators(paleMoss(groundProbability = 0.8, leavesProbability = 0.15, trunkProbability = 0.4))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun paleMoss(groundProbability: Double = 0.0, leavesProbability: Double = 0.0, trunkProbability: Double = 0.0) =
	PaleMoss(groundProbability, leavesProbability, trunkProbability)

/**
 * Appends a [PaleMoss] with the given probabilities.
 *
 * ```kotlin
 * tree("pale_oak") {
 *     decorators { paleMoss(groundProbability = 0.8, leavesProbability = 0.15, trunkProbability = 0.4) }
 * }
 * ```
 */
fun MutableList<TreeDecorator>.paleMoss(
	groundProbability: Double = 0.0,
	leavesProbability: Double = 0.0,
	trunkProbability: Double = 0.0,
) {
	this += PaleMoss(groundProbability, leavesProbability, trunkProbability)
}
