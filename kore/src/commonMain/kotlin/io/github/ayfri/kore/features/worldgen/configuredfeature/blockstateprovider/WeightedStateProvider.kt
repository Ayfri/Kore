package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import kotlinx.serialization.Serializable

/**
 * Picks one of [entries] at random, each entry being drawn proportionally to its weight.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted_state_provider
 *
 * @property entries The weighted providers to pick from, which cannot be empty.
 */
@Serializable
data class WeightedStateProvider(
	var entries: List<WeightedStateProviderEntry> = emptyList(),
) : BlockStateProvider()

/**
 * A single entry of a [WeightedStateProvider]: [data] is picked proportionally to [weight].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted_state_provider
 *
 * @property weight The relative chance of picking [data], which has to be at least `1`.
 * @property data The provider used when this entry is picked.
 */
@Serializable
data class WeightedStateProviderEntry(
	var weight: Int = 1,
	var data: BlockStateProvider = SimpleStateProvider(),
) : BlockStateProviderScope

/**
 * Builder scope for declaring the entries of a [WeightedStateProvider] via [weightedStateProvider].
 *
 * [entry] is an extension on this class, so it only resolves inside a `weightedStateProvider { }` block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted_state_provider
 *
 * @property entries The entries appended so far.
 */
class WeightedStateProviderScope : BlockStateProviderScope {
	val entries = mutableListOf<WeightedStateProviderEntry>()
}

/**
 * Creates a `weighted_state_provider`, picking one of the entries declared in [block] proportionally to its weight.
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
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted_state_provider
 */
fun BlockStateProviderScope.weightedStateProvider(block: WeightedStateProviderScope.() -> Unit) =
	WeightedStateProvider(WeightedStateProviderScope().apply(block).entries)

/**
 * Appends an entry picking [data] proportionally to [weight].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted_state_provider
 */
fun WeightedStateProviderScope.entry(data: BlockStateProvider, weight: Int = 1) {
	entries += WeightedStateProviderEntry(weight, data)
}

/**
 * Appends an entry placing [state] proportionally to [weight], a shorthand for a [SimpleStateProvider] entry.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted_state_provider
 */
fun WeightedStateProviderScope.entry(state: BlockState, weight: Int = 1) {
	entries += WeightedStateProviderEntry(weight, SimpleStateProvider(state))
}

/**
 * Appends an entry placing the default block state of [block] proportionally to [weight].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted_state_provider
 */
fun WeightedStateProviderScope.entry(block: BlockArgument, weight: Int = 1) {
	entries += WeightedStateProviderEntry(weight, SimpleStateProvider(blockState(block)))
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
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#weighted_state_provider
 */
fun WeightedStateProviderScope.entry(weight: Int = 1, block: WeightedStateProviderEntry.() -> Unit) {
	entries += WeightedStateProviderEntry(weight).apply(block)
}
