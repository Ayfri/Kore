package features.worldgen.configuredfeature.configurations.tree.foliageplacer

import features.worldgen.configuredfeature.configurations.Tree
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class FancyFoliagePlacer(
	override var radius: IntProvider = constant(0),
	override var offset: IntProvider = constant(0),
	var height: Int = 0,
) : FoliagePlacer()

fun Tree.fancyFoliagePlacer(
	radius: IntProvider = constant(0),
	offset: IntProvider = constant(0),
	height: Int = 0,
	block: FancyFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = FancyFoliagePlacer(radius, offset, height).apply(block)
}
