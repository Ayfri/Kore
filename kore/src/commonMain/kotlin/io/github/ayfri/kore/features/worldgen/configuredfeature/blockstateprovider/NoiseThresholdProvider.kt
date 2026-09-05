package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.data.block.blockStateStone
import kotlinx.serialization.Serializable

/**
 * Picks a block state by comparing a sampled Perlin [noise] against [threshold]: below it a state of [lowStates] is
 * picked, above it a state of [highStates] is picked with a probability of [highChance], [defaultState] otherwise.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#noise_threshold_provider
 *
 * @property seed The seed of the noise, positions sharing a seed sampling the same values.
 * @property noise The noise parameters.
 * @property scale The factor the position is multiplied by before sampling, larger values giving smaller patches.
 * @property threshold The sampled value separating [lowStates] from [highStates], between `-1.0` and `1.0`.
 * @property highChance The probability of picking a state of [highStates] above the threshold, between `0.0` and `1.0`.
 * @property defaultState The block state placed above the threshold when [highChance] does not pass.
 * @property lowStates The block states picked below the threshold.
 * @property highStates The block states picked above the threshold.
 */
@Serializable
data class NoiseThresholdProvider(
	override var seed: Long = 0,
	override var noise: Noise = Noise(),
	override var scale: Double = 0.0,
	var threshold: Double = 0.0,
	var highChance: Double = 0.0,
	var defaultState: BlockState = blockStateStone(),
	var lowStates: List<BlockState> = emptyList(),
	var highStates: List<BlockState> = emptyList(),
) : BlockStateProvider(), NoiseBasedStateProvider

/**
 * Creates a `noise_threshold_provider`, picking between [NoiseThresholdProvider.lowStates] and
 * [NoiseThresholdProvider.highStates] depending on a sampled Perlin noise.
 *
 * ```kotlin
 * simpleBlock("grass_patch") {
 *     toPlace = noiseThresholdProvider {
 *         seed = 2345
 *         scale = 0.05
 *         threshold = -0.8
 *         highChance = 0.31
 *         noise(-3, 1.0, 1.0)
 *         defaultState = blockState(Blocks.GRASS_BLOCK)
 *         lowStates(Blocks.PODZOL)
 *         highStates(Blocks.COARSE_DIRT)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#noise_threshold_provider
 */
fun BlockStateProviderScope.noiseThresholdProvider(block: NoiseThresholdProvider.() -> Unit = {}) =
	NoiseThresholdProvider().apply(block)

/** Sets [NoiseThresholdProvider.lowStates] to [states], the block states picked below the threshold. */
fun NoiseThresholdProvider.lowStates(vararg states: BlockState) {
	lowStates = states.toList()
}

/** Sets [NoiseThresholdProvider.lowStates] to the default block state of each of [blocks]. */
fun NoiseThresholdProvider.lowStates(vararg blocks: BlockArgument) {
	lowStates = blocks.map { blockState(it) }
}

/** Sets [NoiseThresholdProvider.highStates] to [states], the block states picked above the threshold. */
fun NoiseThresholdProvider.highStates(vararg states: BlockState) {
	highStates = states.toList()
}

/** Sets [NoiseThresholdProvider.highStates] to the default block state of each of [blocks]. */
fun NoiseThresholdProvider.highStates(vararg blocks: BlockArgument) {
	highStates = blocks.map { blockState(it) }
}
