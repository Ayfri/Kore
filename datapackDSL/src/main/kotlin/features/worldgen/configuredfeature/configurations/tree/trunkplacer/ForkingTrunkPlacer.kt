package features.worldgen.configuredfeature.configurations.tree.trunkplacer

import features.worldgen.configuredfeature.configurations.Tree
import kotlinx.serialization.Serializable

@Serializable
data class ForkingTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
) : TrunkPlacer()

fun Tree.forkingTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	block: ForkingTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = ForkingTrunkPlacer(baseHeight, heightRandA, heightRandB).apply(block)
}
