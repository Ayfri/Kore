package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import kotlinx.serialization.Serializable

/**
 * Places a straight, one block wide trunk, with the foliage attached to the block above it. Vanilla uses it for the oak,
 * birch, spruce, pine, jungle and swamp oak trees and for the jungle bushes.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property baseHeight Base height of the trunk, `0` to `32`, `4` for vanilla oaks.
 * @property heightRandA Upper bound of the first random height added to [baseHeight], `0` to `24`, `2` for vanilla oaks.
 * @property heightRandB Upper bound of the second random height added to [baseHeight], `0` to `24`, `0` for vanilla oaks.
 */
@Serializable
data class StraightTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
) : TrunkPlacer()

/**
 * Sets [Tree.trunkPlacer] to a [StraightTrunkPlacer].
 *
 * ```kotlin
 * tree("oak") {
 *     straightTrunkPlacer(baseHeight = 4, heightRandA = 2)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.straightTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	block: StraightTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = StraightTrunkPlacer(baseHeight, heightRandA, heightRandB).apply(block)
}
