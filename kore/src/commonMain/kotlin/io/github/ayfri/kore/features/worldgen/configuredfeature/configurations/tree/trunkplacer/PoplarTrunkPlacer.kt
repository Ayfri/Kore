package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places a straight poplar trunk with branches where the foliage starts.
 *
 * @property branchAmount The number of branches, between `1` and `4`.
 * @property trunkHeightAboveBranches The trunk height above the branches, between `0` and `8`.
 */
@Serializable
data class PoplarTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
	var branchAmount: IntProvider = ConstantIntProvider(1),
	var trunkHeightAboveBranches: IntProvider = ConstantIntProvider(0),
) : TrunkPlacer()

fun Tree.poplarTrunkPlacer(block: PoplarTrunkPlacer.() -> Unit = {}) {
	trunkPlacer = PoplarTrunkPlacer().apply(block)
}
