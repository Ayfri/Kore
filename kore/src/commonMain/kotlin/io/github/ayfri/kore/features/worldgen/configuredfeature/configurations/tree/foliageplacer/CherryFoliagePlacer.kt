package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

@Serializable
data class CherryFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var height: IntProvider = ConstantIntProvider(0),
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
