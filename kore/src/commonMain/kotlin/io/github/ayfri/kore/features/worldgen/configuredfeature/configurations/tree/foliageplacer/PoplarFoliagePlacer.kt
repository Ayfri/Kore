package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places poplar leaves in a rhombus shape facing one of two directions.
 *
 * @property height The foliage height, between `5` and `16`.
 * @property sideHoleChance The chance to remove a leaf on the foliage edges, between `0.0` and `1.0`.
 */
@Serializable
data class PoplarFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var height: IntProvider = ConstantIntProvider(5),
	var sideHoleChance: Double = 0.0,
) : FoliagePlacer()

fun Tree.poplarFoliagePlacer(block: PoplarFoliagePlacer.() -> Unit = {}) {
	foliagePlacer = PoplarFoliagePlacer().apply(block)
}
