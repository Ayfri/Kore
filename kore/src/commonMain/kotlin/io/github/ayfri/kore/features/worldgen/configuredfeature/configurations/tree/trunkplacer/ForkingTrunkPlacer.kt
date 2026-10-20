package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import kotlinx.serialization.Serializable

/**
 * Places the trunk of the acacia trees, forking into one or two branches that each carry a canopy.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property baseHeight Base height of the trunk, `0` to `32`, `5` for vanilla acacias.
 * @property heightRandA Upper bound of the first random height added to [baseHeight], `0` to `24`, `2` for vanilla acacias.
 * @property heightRandB Upper bound of the second random height added to [baseHeight], `0` to `24`, `2` for vanilla acacias.
 */
@Serializable
data class ForkingTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
) : TrunkPlacer()

/**
 * Sets [Tree.trunkPlacer] to a [ForkingTrunkPlacer].
 *
 * ```kotlin
 * tree("acacia") {
 *     forkingTrunkPlacer(baseHeight = 5, heightRandA = 2, heightRandB = 2)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.forkingTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	block: ForkingTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = ForkingTrunkPlacer(baseHeight, heightRandA, heightRandB).apply(block)
}
