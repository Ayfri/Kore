package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import kotlinx.serialization.Serializable

/**
 * Places the 2x2 trunk of the mega pine and mega spruce trees, topped by a single block.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property baseHeight Base height of the trunk, `0` to `32`, `13` for vanilla trees.
 * @property heightRandA Upper bound of the first random height added to [baseHeight], `0` to `24`, `2` for vanilla trees.
 * @property heightRandB Upper bound of the second random height added to [baseHeight], `0` to `24`, `14` for vanilla trees.
 */
@Serializable
data class GiantTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
) : TrunkPlacer()

/**
 * Sets [Tree.trunkPlacer] to a [GiantTrunkPlacer].
 *
 * ```kotlin
 * tree("mega_pine") {
 *     giantTrunkPlacer(baseHeight = 13, heightRandA = 2, heightRandB = 14)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.giantTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	block: GiantTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = GiantTrunkPlacer(baseHeight, heightRandA, heightRandB).apply(block)
}
