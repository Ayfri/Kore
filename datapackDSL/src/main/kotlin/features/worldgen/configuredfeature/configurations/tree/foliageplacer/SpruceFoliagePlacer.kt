package features.worldgen.configuredfeature.configurations.tree.foliageplacer

import features.worldgen.configuredfeature.configurations.Tree
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class SpruceFoliagePlacer(
	override var radius: IntProvider = constant(0),
	override var offset: IntProvider = constant(0),
	var trunkHeight: IntProvider = constant(0),
) : FoliagePlacer()

fun Tree.spruceFoliagePlacer(
	radius: IntProvider = constant(0),
	offset: IntProvider = constant(0),
	trunkHeight: IntProvider = constant(0),
	block: SpruceFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = SpruceFoliagePlacer(radius, offset, trunkHeight).apply(block)
}
