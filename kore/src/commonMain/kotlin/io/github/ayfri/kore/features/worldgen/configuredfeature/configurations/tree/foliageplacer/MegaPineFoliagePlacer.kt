package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

@Serializable
data class MegaPineFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var crownHeight: IntProvider = ConstantIntProvider(0),
) : FoliagePlacer()

fun Tree.megaPineFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	crownHeight: IntProvider = ConstantIntProvider(0),
	block: MegaPineFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = MegaPineFoliagePlacer(radius, offset, crownHeight).apply(block)
}
