package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

@Serializable
data class SpruceFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var trunkHeight: IntProvider = ConstantIntProvider(0),
) : FoliagePlacer()

fun Tree.spruceFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	trunkHeight: IntProvider = ConstantIntProvider(0),
	block: SpruceFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = SpruceFoliagePlacer(radius, offset, trunkHeight).apply(block)
}
