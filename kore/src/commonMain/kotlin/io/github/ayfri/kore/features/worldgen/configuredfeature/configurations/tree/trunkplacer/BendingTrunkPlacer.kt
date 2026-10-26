package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the crooked trunk of the azalea trees, bending sideways at the top, with a horizontal branch on top of it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property bendLength Length of the horizontal branch on top of the trunk, `1` to `64`, `1` to `2` for vanilla azaleas.
 * @property minHeightForLeaves Height from which the trunk blocks get leaves attached, at least `1`. `null` leaves it
 * out, so the game uses `1`; vanilla azaleas use `3`.
 */
@Serializable
data class BendingTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
	var bendLength: IntProvider = ConstantIntProvider(0),
	var minHeightForLeaves: Int? = null,
) : TrunkPlacer()

/**
 * Sets [Tree.trunkPlacer] to a [BendingTrunkPlacer].
 *
 * ```kotlin
 * tree("azalea") {
 *     bendingTrunkPlacer(baseHeight = 4, heightRandA = 2, bendLength = uniform(1, 2), minHeightForLeaves = 3)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.bendingTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	bendLength: IntProvider = ConstantIntProvider(0),
	minHeightForLeaves: Int? = null,
	block: BendingTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = BendingTrunkPlacer(baseHeight, heightRandA, heightRandB, bendLength, minHeightForLeaves).apply(block)
}

/**
 * Sets [Tree.trunkPlacer] to a [BendingTrunkPlacer] bending the trunk by the constant [bendLength].
 *
 * ```kotlin
 * tree("azalea") {
 *     bendingTrunkPlacer(baseHeight = 4, heightRandA = 2, bendLength = 2, minHeightForLeaves = 3)
 * }
 * ```
 */
fun Tree.bendingTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	bendLength: Int,
	minHeightForLeaves: Int? = null,
	block: BendingTrunkPlacer.() -> Unit = {},
) = bendingTrunkPlacer(baseHeight, heightRandA, heightRandB, ConstantIntProvider(bendLength), minHeightForLeaves, block)

/**
 * Sets [BendingTrunkPlacer.bendLength] to [value], how far the trunk bends.
 *
 * ```kotlin
 * bendingTrunkPlacer(baseHeight = 4) { bendLength(uniform(1, 2)) }
 * ```
 */
fun BendingTrunkPlacer.bendLength(value: IntProvider) {
	bendLength = value
}

/** Sets [BendingTrunkPlacer.bendLength] to the constant [value]. */
fun BendingTrunkPlacer.bendLength(value: Int) = bendLength(ConstantIntProvider(value))
