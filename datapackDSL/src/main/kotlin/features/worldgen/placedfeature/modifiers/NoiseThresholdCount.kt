package features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

@Serializable
data class NoiseThresholdCount(
	var noiseLevel: Double = 0.0,
	var belowNoise: Int = 0,
	var aboveNoise: Int = 0,
) : PlacementModifier()

fun noiseThresholdCount(
	noiseLevel: Double = 0.0,
	belowNoise: Int = 0,
	aboveNoise: Int = 0,
	block: NoiseThresholdCount.() -> Unit = {},
) = NoiseThresholdCount(noiseLevel, belowNoise, aboveNoise).apply(block)

fun MutableList<PlacementModifier>.noiseThresholdCount(
	noiseLevel: Double = 0.0,
	belowNoise: Int = 0,
	aboveNoise: Int = 0,
	block: NoiseThresholdCount.() -> Unit = {},
) {
	this += NoiseThresholdCount(noiseLevel, belowNoise, aboveNoise).apply(block)
}
