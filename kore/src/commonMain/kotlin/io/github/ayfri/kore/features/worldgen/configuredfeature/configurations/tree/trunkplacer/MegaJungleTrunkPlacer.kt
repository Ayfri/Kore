package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import kotlinx.serialization.Serializable

/**
 * Places the 2x2 trunk of the mega jungle trees, with branches in random directions in its upper half.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property baseHeight Base height of the trunk, `0` to `32`, `10` for vanilla mega jungle trees.
 * @property heightRandA Upper bound of the first random height added to [baseHeight], `0` to `24`, `2` for vanilla trees.
 * @property heightRandB Upper bound of the second random height added to [baseHeight], `0` to `24`, `19` for vanilla trees.
 */
@Serializable
data class MegaJungleTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
) : TrunkPlacer()

/**
 * Sets [Tree.trunkPlacer] to a [MegaJungleTrunkPlacer].
 *
 * ```kotlin
 * tree("mega_jungle") {
 *     megaJungleTrunkPlacer(baseHeight = 10, heightRandA = 2, heightRandB = 19)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.megaJungleTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	block: MegaJungleTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = MegaJungleTrunkPlacer(baseHeight, heightRandA, heightRandB).apply(block)
}
