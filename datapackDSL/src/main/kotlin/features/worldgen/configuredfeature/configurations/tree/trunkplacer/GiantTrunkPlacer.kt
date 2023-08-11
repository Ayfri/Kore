package features.worldgen.configuredfeature.configurations.tree.trunkplacer

import features.worldgen.configuredfeature.configurations.Tree
import kotlinx.serialization.Serializable

@Serializable
data class GiantTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
) : TrunkPlacer()

fun Tree.giantTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	block: GiantTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = GiantTrunkPlacer(baseHeight, heightRandA, heightRandB).apply(block)
}
