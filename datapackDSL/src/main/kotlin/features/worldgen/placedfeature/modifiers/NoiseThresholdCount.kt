package features.worldgen.placedfeature.modifiers

import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class NoiseThresholdCount(
	var noiseLevel: Double = 0.0,
	var belowNoise: Int = 0,
	var aboveNoise: Int = 0,
) : PlacementModifier()

fun PlacedFeature.noiseThresholdCount(
	noiseLevel: Double = 0.0,
	belowNoise: Int = 0,
	aboveNoise: Int = 0,
	block: NoiseThresholdCount.() -> Unit = {},
) {
	placementModifiers += NoiseThresholdCount(noiseLevel, belowNoise, aboveNoise).apply(block)
}
