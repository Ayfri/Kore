package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import kotlinx.serialization.Serializable

/**
 * Turns one of the logs of the trunk into a creaking heart, which spawns a creaking. Vanilla uses it for the
 * `pale_oak_creaking` tree.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property probability Chance of placing the creaking heart, `0.0` to `1.0`, `1.0` for the vanilla `pale_oak_creaking`.
 */
@Serializable
data class CreakingHeart(
	var probability: Double = 0.0,
) : TreeDecorator()

/**
 * Creates a [CreakingHeart] with the given [probability].
 *
 * ```kotlin
 * tree("pale_oak_creaking") {
 *     decorators(paleMoss(0.8, 0.15, 0.4), creakingHeart(1.0))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun creakingHeart(probability: Double = 0.0) = CreakingHeart(probability)

/**
 * Appends a [CreakingHeart] with the given [probability].
 *
 * ```kotlin
 * tree("pale_oak_creaking") {
 *     decorators { creakingHeart(1.0) }
 * }
 * ```
 */
fun MutableList<TreeDecorator>.creakingHeart(probability: Double = 0.0) {
	this += CreakingHeart(probability)
}
