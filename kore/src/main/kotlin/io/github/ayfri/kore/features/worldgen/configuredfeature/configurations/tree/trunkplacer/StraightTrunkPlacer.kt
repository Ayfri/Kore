package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import kotlinx.serialization.Serializable

@Serializable
data class StraightTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
) : TrunkPlacer()

fun Tree.straightTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	block: StraightTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = StraightTrunkPlacer(baseHeight, heightRandA, heightRandB).apply(block)
}
