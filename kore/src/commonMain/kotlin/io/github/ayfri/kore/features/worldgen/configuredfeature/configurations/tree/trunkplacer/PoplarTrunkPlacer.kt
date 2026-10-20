package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the trunk of the poplar trees, the 26.3 orange, red and yellow poplars, with branches and a stretch of trunk
 * above them.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property baseHeight Base height of the trunk, `0` to `32`, `7` for vanilla poplars.
 * @property heightRandA Upper bound of the first random height added to [baseHeight], `0` to `24`, `4` for vanilla poplars.
 * @property heightRandB Upper bound of the second random height added to [baseHeight], `0` to `24`, `0` for vanilla poplars.
 * @property branchAmount Number of branches, `1` to `4`, `1` to `4` for vanilla poplars.
 * @property trunkHeightAboveBranches Height of the trunk above the branches, `0` to `8`, `4` for vanilla poplars.
 */
@Serializable
data class PoplarTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
	var branchAmount: IntProvider = ConstantIntProvider(1),
	var trunkHeightAboveBranches: IntProvider = ConstantIntProvider(0),
) : TrunkPlacer()

/**
 * Sets [Tree.trunkPlacer] to a [PoplarTrunkPlacer] configured in [block].
 *
 * ```kotlin
 * tree("poplar") {
 *     poplarTrunkPlacer {
 *         baseHeight = 7
 *         heightRandA = 4
 *         branchAmount = uniform(1, 4)
 *         trunkHeightAboveBranches = constant(4)
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.poplarTrunkPlacer(block: PoplarTrunkPlacer.() -> Unit = {}) {
	trunkPlacer = PoplarTrunkPlacer().apply(block)
}
