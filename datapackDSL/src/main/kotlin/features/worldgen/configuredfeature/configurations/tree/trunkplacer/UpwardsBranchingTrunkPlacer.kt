package features.worldgen.configuredfeature.configurations.tree.trunkplacer

import arguments.types.BlockOrTagArgument
import features.worldgen.configuredfeature.configurations.Tree
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class UpwardsBranchingTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
	var extraBranchSteps: IntProvider = constant(0),
	var extraBranchLength: IntProvider = constant(0),
	var placeBranchPerLogProbability: Double = 0.0,
	var canGrowthThrough: InlinableList<BlockOrTagArgument> = emptyList(),
) : TrunkPlacer()

fun Tree.upwardsBranchingTrunkPlacer(block: UpwardsBranchingTrunkPlacer.() -> Unit = {}) {
	trunkPlacer = UpwardsBranchingTrunkPlacer().apply(block)
}
