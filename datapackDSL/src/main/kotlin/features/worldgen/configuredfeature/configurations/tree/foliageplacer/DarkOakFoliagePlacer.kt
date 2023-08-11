package features.worldgen.configuredfeature.configurations.tree.foliageplacer

import features.worldgen.configuredfeature.configurations.Tree
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class DarkOakFoliagePlacer(
	override var radius: IntProvider = constant(0),
	override var offset: IntProvider = constant(0),
) : FoliagePlacer()

fun Tree.darkOakFoliagePlacer(
	radius: IntProvider = constant(0),
	offset: IntProvider = constant(0),
	block: DarkOakFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = DarkOakFoliagePlacer(radius, offset).apply(block)
}
