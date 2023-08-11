package features.worldgen.configuredfeature.configurations.tree.trunkplacer

import features.worldgen.configuredfeature.configurations.Tree
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.UniformIntProvider
import features.worldgen.intproviders.constant
import features.worldgen.intproviders.uniform
import kotlinx.serialization.Serializable

@Serializable
data class CherryTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
	var branchCount: IntProvider = constant(0),
	var branchHorizontalLength: IntProvider = constant(0),
	var branchStartOffsetFromTop: UniformIntProvider = uniform(0, 0),
	var branchEndOffsetFromTop: IntProvider = constant(0),
) : TrunkPlacer()

fun Tree.cherryTrunkPlacer(block: CherryTrunkPlacer.() -> Unit = {}) {
	trunkPlacer = CherryTrunkPlacer().apply(block)
}
