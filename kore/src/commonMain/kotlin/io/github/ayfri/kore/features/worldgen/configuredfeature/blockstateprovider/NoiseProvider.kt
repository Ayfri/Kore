package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.data.block.BlockState
import kotlinx.serialization.Serializable

/**
 * Picks a block state out of [states] by sampling a single Perlin [noise] at the position.
 *
 * The sampled value is mapped onto the whole [states] list, so the blocks appear as continuous patches instead of
 * being randomized per position.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#noise_provider
 *
 * @property seed The seed of the noise, positions sharing a seed sampling the same values.
 * @property noise The noise parameters.
 * @property scale The factor the position is multiplied by before sampling, larger values giving smaller patches.
 * @property states The block states to pick from, mapped onto the sampled value.
 */
@Serializable
data class NoiseProvider(
	override var seed: Long = 0,
	override var noise: Noise = Noise(),
	override var scale: Double = 0.0,
	override var states: List<BlockState> = emptyList(),
) : BlockStateProvider(), NoiseBasedStateProvider, BlockStatesHolder

/**
 * Creates a `noise_provider`, picking a block state out of [NoiseProvider.states] by sampling a Perlin noise.
 *
 * ```kotlin
 * simpleBlock("mossy_patch") {
 *     toPlace = noiseProvider {
 *         seed = 2345
 *         scale = 0.05
 *         noise(-3, 1.0, 1.0)
 *         states(Blocks.MOSS_BLOCK, Blocks.STONE)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#noise_provider
 */
fun BlockStateProviderScope.noiseProvider(block: NoiseProvider.() -> Unit = {}) = NoiseProvider().apply(block)
