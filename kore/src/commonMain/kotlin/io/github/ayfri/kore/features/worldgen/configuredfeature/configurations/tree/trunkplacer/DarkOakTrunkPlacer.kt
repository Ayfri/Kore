package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import kotlinx.serialization.Serializable

/**
 * Places the 2x2 trunk of the dark oak and pale oak trees, with columns of branches around its top.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property baseHeight Base height of the trunk, `0` to `32`, `6` for vanilla trees.
 * @property heightRandA Upper bound of the first random height added to [baseHeight], `0` to `24`, `2` for vanilla trees.
 * @property heightRandB Upper bound of the second random height added to [baseHeight], `0` to `24`, `1` for vanilla trees.
 */
@Serializable
data class DarkOakTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
) : TrunkPlacer()

/**
 * Sets [Tree.trunkPlacer] to a [DarkOakTrunkPlacer].
 *
 * ```kotlin
 * tree("dark_oak") {
 *     darkOakTrunkPlacer(baseHeight = 6, heightRandA = 2, heightRandB = 1)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.darkOakTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	block: DarkOakTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = DarkOakTrunkPlacer(baseHeight, heightRandA, heightRandB).apply(block)
}
