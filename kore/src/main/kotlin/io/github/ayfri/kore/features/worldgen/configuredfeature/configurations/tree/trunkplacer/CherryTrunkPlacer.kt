package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.UniformIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import kotlinx.serialization.Serializable

@Serializable
data class CherryTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
	var branchCount: IntProvider = constant(0),
	var branchHorizontalLength: IntProvider = constant(0),
	var branchStartOffsetFromTop: UniformIntProvider = uniform(0, 0),
	var branchEndOffsetFromTop: IntProvider = constant(0),
) : TrunkPlacer()

fun Tree.cherryTrunkPlacer(block: CherryTrunkPlacer.() -> Unit = {}) {
	trunkPlacer = CherryTrunkPlacer().apply(block)
}
