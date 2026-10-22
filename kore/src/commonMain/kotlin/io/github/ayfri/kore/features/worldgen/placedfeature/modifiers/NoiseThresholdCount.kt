package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

@Serializable
data class NoiseThresholdCount(
	var noiseLevel: Double = 0.0,
	var belowNoise: Int = 0,
	var aboveNoise: Int = 0,
) : PlacementModifier()

fun PlacementModifiersScope.noiseThresholdCount(
	noiseLevel: Double = 0.0,
	belowNoise: Int = 0,
	aboveNoise: Int = 0,
	block: NoiseThresholdCount.() -> Unit = {},
) {
	placementModifiers += NoiseThresholdCount(noiseLevel, belowNoise, aboveNoise).apply(block)
}
