package features.worldgen.configuredfeature.configurations.tree.foliageplacer

import features.worldgen.configuredfeature.configurations.Tree
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class AcaciaFoliagePlacer(
	override var radius: IntProvider = constant(0),
	override var offset: IntProvider = constant(0),
) : FoliagePlacer()

fun Tree.acaciaFoliagePlacer(
	radius: IntProvider = constant(0),
	offset: IntProvider = constant(0),
	block: AcaciaFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = AcaciaFoliagePlacer(radius, offset).apply(block)
}
