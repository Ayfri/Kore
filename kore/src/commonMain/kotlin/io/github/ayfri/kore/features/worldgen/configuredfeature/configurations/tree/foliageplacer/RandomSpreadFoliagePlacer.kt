package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

@Serializable
data class RandomSpreadFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var foliageHeight: IntProvider = ConstantIntProvider(0),
	var leafPlacementAttempts: Int = 0,
) : FoliagePlacer()

fun Tree.randomSpreadFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	foliageHeight: IntProvider = ConstantIntProvider(0),
	leafPlacementAttempts: Int = 0,
	block: RandomSpreadFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = RandomSpreadFoliagePlacer(radius, offset, foliageHeight, leafPlacementAttempts).apply(block)
}
