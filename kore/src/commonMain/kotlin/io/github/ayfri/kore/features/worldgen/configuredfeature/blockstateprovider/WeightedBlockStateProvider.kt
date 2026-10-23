package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import kotlinx.serialization.Serializable

/**
 * Picks one of [entries] at random, each entry being drawn proportionally to its weight.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted
 *
 * @property entries The weighted providers to pick from, which cannot be empty.
 */
@Serializable
data class WeightedBlockStateProvider(
	var entries: List<WeightedBlockStateProviderEntry> = emptyList(),
) : BlockStateProvider()

/**
 * A single entry of a [WeightedBlockStateProvider]: [data] is picked proportionally to [weight].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted
 *
 * @property weight The relative chance of picking [data], which has to be at least `1`.
 * @property data The provider used when this entry is picked.
 */
@Serializable
data class WeightedBlockStateProviderEntry(
	var weight: Int = 1,
	var data: BlockStateProvider = SimpleBlockStateProvider(),
) : BlockStateProviderScope

/**
 * Builder scope for declaring the entries of a [WeightedBlockStateProvider] via [weightedStateProvider].
 *
 * [entry] is an extension on this class, so it only resolves inside a `weightedStateProvider { }` block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted
 *
 * @property entries The entries appended so far.
 */
class WeightedStateProviderScope : BlockStateProviderScope {
	val entries = mutableListOf<WeightedBlockStateProviderEntry>()
}

/**
 * Creates a `weighted` block state provider, picking one of the entries declared in [block] proportionally to its weight.
 *
 * ```kotlin
 * simpleBlock("random_flower") {
 *     toPlace = weightedStateProvider {
 *         entry(Blocks.DANDELION, weight = 3)
 *         entry(Blocks.POPPY)
 *         entry(weight = 2) {
 *             data = rotatedBlockProvider(Blocks.OAK_LOG)
 *         }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted
 */
fun BlockStateProviderScope.weightedStateProvider(block: WeightedStateProviderScope.() -> Unit) =
	WeightedBlockStateProvider(WeightedStateProviderScope().apply(block).entries)

/**
 * Appends an entry picking [data] proportionally to [weight].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted
 */
fun WeightedStateProviderScope.entry(data: BlockStateProvider, weight: Int = 1) {
	entries += WeightedBlockStateProviderEntry(weight, data)
}

/**
 * Appends an entry placing [state] proportionally to [weight], a shorthand for a [SimpleBlockStateProvider] entry.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted
 */
fun WeightedStateProviderScope.entry(state: BlockState, weight: Int = 1) {
	entries += WeightedBlockStateProviderEntry(weight, SimpleBlockStateProvider(state))
}

/**
 * Appends an entry placing the default block state of [block] proportionally to [weight].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted
 */
fun WeightedStateProviderScope.entry(block: BlockArgument, weight: Int = 1) {
	entries += WeightedBlockStateProviderEntry(weight, SimpleBlockStateProvider(blockState(block)))
}

/**
 * Appends an entry of the given [weight] configured entirely through [block].
 *
 * ```kotlin
 * weightedStateProvider {
 *     entry(weight = 2) {
 *         data = rotatedBlockProvider(Blocks.OAK_LOG)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted
 */
fun WeightedStateProviderScope.entry(weight: Int = 1, block: WeightedBlockStateProviderEntry.() -> Unit) {
	entries += WeightedBlockStateProviderEntry(weight).apply(block)
}
