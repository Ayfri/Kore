package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class BlobFoliagePlacer(
	override var radius: IntProvider = constant(0),
	override var offset: IntProvider = constant(0),
	var height: Int = 0,
) : FoliagePlacer()

fun Tree.blobFoliagePlacer(
	radius: IntProvider = constant(0),
	offset: IntProvider = constant(0),
	height: Int = 0,
	block: BlobFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = BlobFoliagePlacer(radius, offset, height).apply(block)
}
