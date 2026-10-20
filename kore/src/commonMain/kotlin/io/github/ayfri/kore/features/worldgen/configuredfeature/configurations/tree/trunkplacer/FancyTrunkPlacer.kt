package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import kotlinx.serialization.Serializable

/**
 * Places the tall trunk of the large (fancy) oak trees, with branches growing in random directions.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property baseHeight Base height of the trunk, `0` to `32`, `3` for vanilla large oaks.
 * @property heightRandA Upper bound of the first random height added to [baseHeight], `0` to `24`, `11` for vanilla large
 * oaks.
 * @property heightRandB Upper bound of the second random height added to [baseHeight], `0` to `24`, `0` for vanilla large
 * oaks.
 */
@Serializable
data class FancyTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
) : TrunkPlacer()

/**
 * Sets [Tree.trunkPlacer] to a [FancyTrunkPlacer].
 *
 * ```kotlin
 * tree("fancy_oak") {
 *     fancyTrunkPlacer(baseHeight = 3, heightRandA = 11)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.fancyTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	block: FancyTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = FancyTrunkPlacer(baseHeight, heightRandA, heightRandB).apply(block)
}
