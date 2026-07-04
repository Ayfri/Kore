package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoiseProvider(
	var seed: Int = 0,
	var noise: Noise = Noise(),
	var scale: Double = 0.0,
	var states: List<BlockState> = emptyList(),
) : BlockStateProvider()

@Serializable
data class Noise(
	@JsonSerialName("firstOctave")
	var firstOctave: Int = 0,
	var amplitudes: List<Double> = emptyList(),
)

fun noiseProvider(block: NoiseProvider.() -> Unit = {}) = NoiseProvider().apply(block)

fun NoiseProvider.noise(block: Noise.() -> Unit = {}) {
	noise = Noise().apply(block)
}
