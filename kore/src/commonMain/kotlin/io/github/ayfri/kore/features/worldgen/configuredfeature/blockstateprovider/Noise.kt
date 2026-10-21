package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.features.worldgen.noise.NoiseParameters
import io.github.ayfri.kore.features.worldgen.noise.noiseParameters

/**
 * A block state provider sampling a Perlin [noise] to pick between several block states.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider
 *
 * @property seed The seed of the noise, positions sharing a seed sampling the same values.
 * @property noise The noise parameters.
 * @property scale The factor the position is multiplied by before sampling, larger values giving smaller patches.
 */
sealed interface NoiseBasedStateProvider : BlockStateProviderScope {
	var seed: Long
	var noise: NoiseParameters
	var scale: Double
}

/**
 * Sets [NoiseBasedStateProvider.noise] to the parameters built in [block], starting at [baseOctave].
 *
 * ```kotlin
 * noiseProvider {
 *     noise(baseOctave = -3) { amplitudeModifiers(1.0, 1.0) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Noise_settings
 */
fun NoiseBasedStateProvider.noise(baseOctave: Int = 0, block: NoiseParameters.() -> Unit = {}) {
	noise = noiseParameters(baseOctave, block)
}

/**
 * Sets [NoiseBasedStateProvider.noise] to [baseOctave] and [amplitudeModifiers], one modifier per octave, which also sets the octave count.
 *
 * ```kotlin
 * noiseProvider {
 *     noise(-3, 1.0, 1.0)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Noise_settings
 */
fun NoiseBasedStateProvider.noise(baseOctave: Int, vararg amplitudeModifiers: Double) {
	noise = noiseParameters(baseOctave, *amplitudeModifiers)
}

/**
 * A block state provider picking inside a flat list of [states].
 *
 * @property states The block states to pick from.
 */
sealed interface BlockStatesHolder {
	var states: List<BlockState>
}

/**
 * Sets [BlockStatesHolder.states] to [states], the block states the provider picks from.
 *
 * ```kotlin
 * noiseProvider {
 *     states(blockState(Blocks.STONE), blockState(Blocks.ANDESITE))
 * }
 * ```
 */
fun BlockStatesHolder.states(vararg states: BlockState) {
	this.states = states.toList()
}

/**
 * Sets [BlockStatesHolder.states] to the default block state of each of [blocks].
 *
 * ```kotlin
 * noiseProvider {
 *     states(Blocks.STONE, Blocks.ANDESITE)
 * }
 * ```
 */
fun BlockStatesHolder.states(vararg blocks: BlockArgument) {
	states = blocks.map { blockState(it) }
}
