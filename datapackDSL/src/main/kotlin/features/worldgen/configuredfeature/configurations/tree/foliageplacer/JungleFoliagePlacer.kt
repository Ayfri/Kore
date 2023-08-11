package features.worldgen.configuredfeature.configurations.tree.foliageplacer

import features.worldgen.configuredfeature.configurations.Tree
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class JungleFoliagePlacer(
	override var radius: IntProvider = constant(0),
	override var offset: IntProvider = constant(0),
	var height: Int = 0,
) : FoliagePlacer()

fun Tree.jungleFoliagePlacer(
	radius: IntProvider = constant(0),
	offset: IntProvider = constant(0),
	height: Int = 0,
	block: JungleFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = JungleFoliagePlacer(radius, offset, height).apply(block)
}
