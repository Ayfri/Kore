package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class NoiseTresholdProvider(
	var seed: Int = 0,
	var noise: Noise = Noise(),
	var scale: Double = 0.0,
	var treshold: Double = 0.0,
	var highChance: Double = 0.0,
	var defaultState: BlockState = blockStateStone(),
	var lowStates: List<BlockState> = emptyList(),
	var highStates: List<BlockState> = emptyList(),
) : BlockStateProvider()

fun noiseTresholdProvider(block: NoiseTresholdProvider.() -> Unit = {}) = NoiseTresholdProvider().apply(block)

fun NoiseTresholdProvider.noise(block: Noise.() -> Unit = {}) {
	noise = Noise().apply(block)
}
