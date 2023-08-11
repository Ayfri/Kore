package features.worldgen.configuredfeature.configurations.tree.foliageplacer

import features.worldgen.configuredfeature.configurations.Tree
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class CherryFoliagePlacer(
	override var radius: IntProvider = constant(0),
	override var offset: IntProvider = constant(0),
	var height: IntProvider = constant(0),
	var wideBottomLayerHoleChance: Double = 0.0,
	var cornerHoleChance: Double = 0.0,
	var hangingLeavesChance: Double = 0.0,
	var hangingLeavesExtensionChance: Double = 0.0,
) : FoliagePlacer()

fun Tree.cherryFoliagePlacer(
	block: CherryFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = CherryFoliagePlacer().apply(block)
}
