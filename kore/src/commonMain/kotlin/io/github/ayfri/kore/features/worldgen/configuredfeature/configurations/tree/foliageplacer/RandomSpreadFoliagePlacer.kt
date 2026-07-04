package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class RandomSpreadFoliagePlacer(
	override var radius: IntProvider = constant(0),
	override var offset: IntProvider = constant(0),
	var foliageHeight: IntProvider = constant(0),
	var leafPlacementAttempts: Int = 0,
) : FoliagePlacer()

fun Tree.randomSpreadFoliagePlacer(
	radius: IntProvider = constant(0),
	offset: IntProvider = constant(0),
	foliageHeight: IntProvider = constant(0),
	leafPlacementAttempts: Int = 0,
	block: RandomSpreadFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = RandomSpreadFoliagePlacer(radius, offset, foliageHeight, leafPlacementAttempts).apply(block)
}
