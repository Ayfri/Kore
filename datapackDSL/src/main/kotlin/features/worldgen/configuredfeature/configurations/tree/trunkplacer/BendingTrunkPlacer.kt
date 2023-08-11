package features.worldgen.configuredfeature.configurations.tree.trunkplacer

import features.worldgen.configuredfeature.configurations.Tree
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class BendingTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
	var bendLength: IntProvider = constant(0),
	var minHeightForLeaves: Int? = null,
) : TrunkPlacer()

fun Tree.bendingTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	bendLength: IntProvider = constant(0),
	minHeightForLeaves: Int? = null,
	block: BendingTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = BendingTrunkPlacer(baseHeight, heightRandA, heightRandB, bendLength, minHeightForLeaves).apply(block)
}
