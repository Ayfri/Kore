package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

@Serializable
data class PineFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var height: IntProvider = ConstantIntProvider(0),
) : FoliagePlacer()

fun Tree.pineFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	height: IntProvider = ConstantIntProvider(0),
	block: PineFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = PineFoliagePlacer(radius, offset, height).apply(block)
}
