package features.worldgen.configuredfeature.configurations.tree.foliageplacer

import features.worldgen.configuredfeature.configurations.Tree
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class MegaPineFoliagePlacer(
	override var radius: IntProvider = constant(0),
	override var offset: IntProvider = constant(0),
	var crownHeight: IntProvider = constant(0),
) : FoliagePlacer()

fun Tree.megaPineFoliagePlacer(
	radius: IntProvider = constant(0),
	offset: IntProvider = constant(0),
	crownHeight: IntProvider = constant(0),
	block: MegaPineFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = MegaPineFoliagePlacer(radius, offset, crownHeight).apply(block)
}
