package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

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

fun Tree.upwardsBranchingTrunkPlacer(block: UpwardsBranchingTrunkPlacer.() -> Unit = {}) {
	trunkPlacer = UpwardsBranchingTrunkPlacer().apply(block)
}
