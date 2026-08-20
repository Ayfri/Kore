package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import kotlinx.serialization.Serializable

/**
 * Picks a block state out of [states] by sampling two Perlin noises: [slowNoise] selects how many states of the list
 * are in play at the position, [noise] then picks one of them.
 *
 * Vanilla uses it for the nether forest floors, where the variety of the vegetation itself varies across the biome.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#dual_noise_provider
 *
 * @property seed The seed of both noises, positions sharing a seed sampling the same values.
 * @property noise The parameters of the fast noise, picking a state among the ones selected by [slowNoise].
 * @property scale The factor the position is multiplied by before sampling [noise].
 * @property variety The amount of states in play at a position, between `1` and `64`.
 * @property slowNoise The parameters of the slow noise, selecting how many states are in play.
 * @property slowScale The factor the position is multiplied by before sampling [slowNoise].
 * @property states The block states to pick from.
 */
@Serializable
data class DualNoiseProvider(
	override var seed: Long = 0,
	override var noise: Noise = Noise(),
	override var scale: Double = 0.0,
	var variety: IntProvider = constant(1),
	var slowNoise: Noise = Noise(),
	var slowScale: Double = 0.0,
	override var states: List<BlockState> = emptyList(),
) : BlockStateProvider(), NoiseBasedStateProvider, BlockStatesHolder

/**
 * Creates a `dual_noise_provider`, picking a block state out of [DualNoiseProvider.states] by sampling two Perlin
 * noises.
 *
 * ```kotlin
 * simpleBlock("nether_floor") {
 *     toPlace = dualNoiseProvider {
 *         seed = 2345
 *         scale = 0.05
 *         slowScale = 0.005
 *         noise(-3, 1.0, 1.0)
 *         slowNoise(-10, 1.0, 1.0, 1.0, 1.0)
 *         variety(1, 3)
 *         states(Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#dual_noise_provider
 */
fun BlockStateProviderScope.dualNoiseProvider(block: DualNoiseProvider.() -> Unit = {}) = DualNoiseProvider().apply(block)

/**
 * Sets [DualNoiseProvider.variety] to a uniform int provider between [min] and [max], the amount of states in play at
 * a position.
 *
 * Both bounds have to be between `1` and `64`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#dual_noise_provider
 */
fun DualNoiseProvider.variety(min: Int, max: Int) {
	variety = uniform(min, max)
}

/**
 * Sets [DualNoiseProvider.slowNoise] to the parameters built in [block], starting at [firstOctave].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Noise_settings
 */
fun DualNoiseProvider.slowNoise(firstOctave: Int = 0, block: Noise.() -> Unit = {}) {
	slowNoise = Noise(firstOctave).apply(block)
}

/**
 * Sets [DualNoiseProvider.slowNoise] to [firstOctave] and [amplitudes], one amplitude per successive octave.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Noise_settings
 */
fun DualNoiseProvider.slowNoise(firstOctave: Int, vararg amplitudes: Double) {
	slowNoise = Noise(firstOctave, amplitudes.toList())
}
