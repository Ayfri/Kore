package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places the trunk of the mangrove trees, growing extra branches upwards from its logs.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property baseHeight Base height of the trunk, `0` to `32`, `2` for vanilla mangroves.
 * @property heightRandA Upper bound of the first random height added to [baseHeight], `0` to `24`, `1` for vanilla mangroves.
 * @property heightRandB Upper bound of the second random height added to [baseHeight], `0` to `24`, `4` for vanilla mangroves.
 * @property extraBranchSteps Number of steps in which extra branches can grow, at least `1`, `1` to `4` for vanilla mangroves.
 * @property extraBranchLength Length of the extra branches, at least `0`, `0` to `1` for vanilla mangroves.
 * @property placeBranchPerLogProbability Chance of each log producing a branch, `0.0` to `1.0`, `0.5` in vanilla.
 * @property canGrowthThrough A block, a block tag or a list of blocks the trunk can grow through.
 */
@Serializable
data class UpwardsBranchingTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
	var extraBranchSteps: IntProvider = ConstantIntProvider(0),
	var extraBranchLength: IntProvider = ConstantIntProvider(0),
	var placeBranchPerLogProbability: Double = 0.0,
	var canGrowthThrough: InlinableList<BlockOrTagArgument> = emptyList(),
) : TrunkPlacer()

/**
 * Sets [Tree.trunkPlacer] to an [UpwardsBranchingTrunkPlacer] configured in [block].
 *
 * ```kotlin
 * tree("mangrove") {
 *     upwardsBranchingTrunkPlacer {
 *         baseHeight = 2
 *         heightRandA = 1
 *         heightRandB = 4
 *         extraBranchSteps = uniform(1, 4)
 *         extraBranchLength = uniform(0, 1)
 *         placeBranchPerLogProbability = 0.5
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.upwardsBranchingTrunkPlacer(block: UpwardsBranchingTrunkPlacer.() -> Unit = {}) {
	trunkPlacer = UpwardsBranchingTrunkPlacer().apply(block)
}
