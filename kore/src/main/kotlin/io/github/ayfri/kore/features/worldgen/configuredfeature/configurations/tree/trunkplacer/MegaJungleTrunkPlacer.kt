package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import kotlinx.serialization.Serializable

@Serializable
data class MegaJungleTrunkPlacer(
	override var baseHeight: Int = 0,
	override var heightRandA: Int = 0,
	override var heightRandB: Int = 0,
) : TrunkPlacer()

fun Tree.megaJungleTrunkPlacer(
	baseHeight: Int = 0,
	heightRandA: Int = 0,
	heightRandB: Int = 0,
	block: MegaJungleTrunkPlacer.() -> Unit = {},
) {
	trunkPlacer = MegaJungleTrunkPlacer(baseHeight, heightRandA, heightRandB).apply(block)
}
