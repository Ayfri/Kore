package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.UniformIntProvider
import kotlinx.serialization.Serializable

/**
 * Places the trunk of the cherry trees, splitting into branches near its top that carry the foliage.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property branchCount Number of branches, `1` to `3`, `1` to `3` for vanilla cherries.
 * @property branchHorizontalLength Horizontal length of the branches, `2` to `16`, `2` to `4` for vanilla cherries.
 * @property branchStartOffsetFromTop Where the branches start, relative to the top of the trunk, `-16` to `0`. It needs at
 * least two blocks of variation (`max > min`) for both branches to fit, `-4` to `-3` for vanilla cherries.
 * @property branchEndOffsetFromTop Where the branches end, relative to the top of the trunk, `-16` to `16`, `-1` to `0` for
 * vanilla cherries.
 */
@Serializable
data class CherryTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
	var branchCount: IntProvider = ConstantIntProvider(0),
	var branchHorizontalLength: IntProvider = ConstantIntProvider(0),
	var branchStartOffsetFromTop: UniformIntProvider = UniformIntProvider(0, 0),
	var branchEndOffsetFromTop: IntProvider = ConstantIntProvider(0),
) : TrunkPlacer()

/**
 * Sets [Tree.trunkPlacer] to a [CherryTrunkPlacer] configured in [block].
 *
 * ```kotlin
 * tree("cherry") {
 *     cherryTrunkPlacer {
 *         baseHeight = 7
 *         heightRandA = 1
 *         branchCount = uniform(1, 3)
 *         branchHorizontalLength = uniform(2, 4)
 *         branchStartOffsetFromTop = uniform(-4, -3)
 *         branchEndOffsetFromTop = uniform(-1, 0)
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.cherryTrunkPlacer(block: CherryTrunkPlacer.() -> Unit = {}) {
	trunkPlacer = CherryTrunkPlacer().apply(block)
}

/**
 * Sets [CherryTrunkPlacer.branchCount] to [value], the number of branches.
 *
 * ```kotlin
 * cherryTrunkPlacer { branchCount(uniform(1, 3)) }
 * ```
 */
fun CherryTrunkPlacer.branchCount(value: IntProvider) {
	branchCount = value
}

/** Sets [CherryTrunkPlacer.branchCount] to the constant [value]. */
fun CherryTrunkPlacer.branchCount(value: Int) = branchCount(ConstantIntProvider(value))

/**
 * Sets [CherryTrunkPlacer.branchEndOffsetFromTop] to [value], where branches end relative to the top of the trunk.
 *
 * ```kotlin
 * cherryTrunkPlacer { branchEndOffsetFromTop(uniform(-1, 0)) }
 * ```
 */
fun CherryTrunkPlacer.branchEndOffsetFromTop(value: IntProvider) {
	branchEndOffsetFromTop = value
}

/** Sets [CherryTrunkPlacer.branchEndOffsetFromTop] to the constant [value]. */
fun CherryTrunkPlacer.branchEndOffsetFromTop(value: Int) = branchEndOffsetFromTop(ConstantIntProvider(value))

/**
 * Sets [CherryTrunkPlacer.branchHorizontalLength] to [value], the horizontal length of the branches.
 *
 * ```kotlin
 * cherryTrunkPlacer { branchHorizontalLength(uniform(2, 4)) }
 * ```
 */
fun CherryTrunkPlacer.branchHorizontalLength(value: IntProvider) {
	branchHorizontalLength = value
}

/** Sets [CherryTrunkPlacer.branchHorizontalLength] to the constant [value]. */
fun CherryTrunkPlacer.branchHorizontalLength(value: Int) = branchHorizontalLength(ConstantIntProvider(value))
