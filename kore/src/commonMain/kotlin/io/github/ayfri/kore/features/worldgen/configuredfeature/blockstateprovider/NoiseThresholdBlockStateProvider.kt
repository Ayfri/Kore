package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.noise.NoiseParameters
import kotlinx.serialization.Serializable

/**
 * Picks a block state by comparing a sampled Perlin [noise] against [threshold]: below it a state of [lowStates] is
 * picked, above it a state of [highStates] is picked with a probability of [highChance], [defaultState] otherwise.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#noise_threshold
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
data class NoiseThresholdBlockStateProvider(
	override var seed: Long = 0,
	override var noise: NoiseParameters = NoiseParameters(),
	override var scale: Double = 0.0,
	var threshold: Double = 0.0,
	var highChance: Double = 0.0,
	var defaultState: BlockState = blockStateStone(),
	var lowStates: List<BlockState> = emptyList(),
	var highStates: List<BlockState> = emptyList(),
) : BlockStateProvider(), NoiseBasedStateProvider

/**
 * Creates a `noise_threshold` block state provider, picking between [NoiseThresholdBlockStateProvider.lowStates] and
 * [NoiseThresholdBlockStateProvider.highStates] depending on a sampled Perlin noise.
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
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#noise_threshold
 */
fun BlockStateProviderScope.noiseThresholdProvider(block: NoiseThresholdBlockStateProvider.() -> Unit = {}) =
	NoiseThresholdBlockStateProvider().apply(block)

/** Sets [NoiseThresholdBlockStateProvider.lowStates] to [states], the block states picked below the threshold. */
fun NoiseThresholdBlockStateProvider.lowStates(vararg states: BlockState) {
	lowStates = states.toList()
}

/** Sets [NoiseThresholdBlockStateProvider.lowStates] to the default block state of each of [blocks]. */
fun NoiseThresholdBlockStateProvider.lowStates(vararg blocks: BlockArgument) {
	lowStates = blocks.map { blockState(it) }
}

/** Sets [NoiseThresholdBlockStateProvider.highStates] to [states], the block states picked above the threshold. */
fun NoiseThresholdBlockStateProvider.highStates(vararg states: BlockState) {
	highStates = states.toList()
}

/** Sets [NoiseThresholdBlockStateProvider.highStates] to the default block state of each of [blocks]. */
fun NoiseThresholdBlockStateProvider.highStates(vararg blocks: BlockArgument) {
	highStates = blocks.map { blockState(it) }
}
