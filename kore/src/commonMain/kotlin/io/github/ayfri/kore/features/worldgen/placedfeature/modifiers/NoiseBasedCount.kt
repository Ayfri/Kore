package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class NoiseBasedCount(
	var noiseToCountRatio: Int = 0,
	var noiseFactor: Double = 0.0,
	var noiseOffset: Double? = null,
) : PlacementModifier()

fun PlacedFeature.noiseBasedCount(
	noiseToCountRatio: Int = 0,
	noiseFactor: Double = 0.0,
	noiseOffset: Double? = null,
	block: NoiseBasedCount.() -> Unit = {},
) {
	placementModifiers += NoiseBasedCount(noiseToCountRatio, noiseFactor, noiseOffset).apply(block)
}
