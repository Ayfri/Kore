package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable

/**
 * The Perlin noise parameters sampled by the noise-based block state providers.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Noise_settings
 *
 * @property firstOctave The octave the first amplitude applies to, serialized as `firstOctave` and not snake_cased.
 * @property amplitudes The amplitude of each successive octave, an amplitude of `0.0` skipping its octave.
 */
@Serializable
data class Noise(
	@JsonSerialName("firstOctave")
	var firstOctave: Int = 0,
	var amplitudes: List<Double> = emptyList(),
)

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
	var noise: Noise
	var scale: Double
}

/**
 * Sets [NoiseBasedStateProvider.noise] to the parameters built in [block], starting at [firstOctave].
 *
 * ```kotlin
 * noiseProvider {
 *     noise(firstOctave = -3) { amplitudes = listOf(1.0, 1.0) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Noise_settings
 */
fun NoiseBasedStateProvider.noise(firstOctave: Int = 0, block: Noise.() -> Unit = {}) {
	noise = Noise(firstOctave).apply(block)
}

/**
 * Sets [NoiseBasedStateProvider.noise] to [firstOctave] and [amplitudes], one amplitude per successive octave.
 *
 * ```kotlin
 * noiseProvider {
 *     noise(-3, 1.0, 1.0)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Noise_settings
 */
fun NoiseBasedStateProvider.noise(firstOctave: Int, vararg amplitudes: Double) {
	noise = Noise(firstOctave, amplitudes.toList())
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
