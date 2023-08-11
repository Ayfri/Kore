package features.worldgen.configuredfeature.blockstateprovider

import data.block.BlockState
import features.worldgen.intproviders.UniformIntProvider
import features.worldgen.intproviders.uniform
import kotlinx.serialization.Serializable

@Serializable
data class DualNoiseProvider(
	var seed: Int = 0,
	var noise: Noise = Noise(),
	var scale: Double = 0.0,
	var variety: UniformIntProvider = uniform(0, 0),
	var slowNoise: Noise = Noise(),
	var slowScale: Double = 0.0,
	var states: List<BlockState> = emptyList(),
) : BlockStateProvider()

fun dualNoiseProvider(block: DualNoiseProvider.() -> Unit = {}) = DualNoiseProvider().apply(block)

fun DualNoiseProvider.noise(block: Noise.() -> Unit = {}) {
	noise = Noise().apply(block)
}

fun DualNoiseProvider.variety(min: Int, max: Int) {
	variety = uniform(min, max)
}

fun DualNoiseProvider.slowNoise(block: Noise.() -> Unit = {}) {
	slowNoise = Noise().apply(block)
}
