package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class PineFoliagePlacer(
	override var radius: IntProvider = constant(0),
	override var offset: IntProvider = constant(0),
	var height: IntProvider = constant(0),
) : FoliagePlacer()

fun Tree.pineFoliagePlacer(
	radius: IntProvider = constant(0),
	offset: IntProvider = constant(0),
	height: IntProvider = constant(0),
	block: PineFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = PineFoliagePlacer(radius, offset, height).apply(block)
}
