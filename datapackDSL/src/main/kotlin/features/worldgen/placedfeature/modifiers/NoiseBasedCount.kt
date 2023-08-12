package features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

@Serializable
data class NoiseBasedCount(
	var noiseToCountRatio: Int = 0,
	var noiseFactor: Double = 0.0,
	var noiseOffset: Double? = null,
) : PlacementModifier()

fun noiseBasedCount(
	noiseToCountRatio: Int = 0,
	noiseFactor: Double = 0.0,
	noiseOffset: Double? = null,
	block: NoiseBasedCount.() -> Unit = {},
) = NoiseBasedCount(noiseToCountRatio, noiseFactor, noiseOffset).apply(block)

fun MutableList<PlacementModifier>.noiseBasedCount(
	noiseToCountRatio: Int = 0,
	noiseFactor: Double = 0.0,
	noiseOffset: Double? = null,
	block: NoiseBasedCount.() -> Unit = {},
) {
	this += NoiseBasedCount(noiseToCountRatio, noiseFactor, noiseOffset).apply(block)
}
