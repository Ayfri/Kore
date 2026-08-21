package io.github.ayfri.kore.features.worldgen.heightproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns a Y level drawn from a weighted list of nested height providers, each entry being picked with a
 * probability of its weight over the total weight.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 *
 * @property distribution The entries to draw from, which must not be empty.
 */
@Serializable
@SerialName("minecraft:weighted_list")
data class WeightedListHeightProvider(
	var distribution: List<WeightedListHeightProviderEntry>,
) : HeightProvider

/**
 * One entry of a [WeightedListHeightProvider].
 *
 * @property weight Relative chance of drawing this entry, compared to the other entries of the list.
 * @property data The height provider used when this entry is drawn.
 */
@Serializable
data class WeightedListHeightProviderEntry(
	var weight: Int,
	var data: HeightProvider,
)

/**
 * Builder scope for the entries of a [WeightedListHeightProvider], adding [entry] on top of the height provider
 * builders inherited from [HeightProviderScope].
 */
class WeightedListHeightProviderBuilder internal constructor() : HeightProviderScope {
	internal val entries = mutableListOf<WeightedListHeightProviderEntry>()

	/** Adds an entry drawing from [data] with a relative chance of [weight]. */
	fun entry(weight: Int, data: HeightProvider) {
		entries += WeightedListHeightProviderEntry(weight, data)
	}
}

/**
 * Creates a `weighted_list` height provider drawing from [distribution], which must not be empty.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.weightedListHeightProvider(
	distribution: List<WeightedListHeightProviderEntry>,
) = WeightedListHeightProvider(distribution)

/**
 * Creates a `weighted_list` height provider drawing from [distribution], each pair associating a weight with the
 * height provider it draws.
 *
 * ```kotlin
 * heightRange(weightedListHeightProvider(3 to constantAbsolute(32), 1 to uniformHeightProvider(64, 96)))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.weightedListHeightProvider(
	vararg distribution: Pair<Int, HeightProvider>,
) = weightedListHeightProvider(distribution.map { WeightedListHeightProviderEntry(it.first, it.second) })

/**
 * Creates a `weighted_list` height provider whose entries are added in [block].
 *
 * ```kotlin
 * heightRange(weightedListHeightProvider {
 *     entry(3, constantAbsolute(32))
 *     entry(1, uniformHeightProvider(64, 96))
 * })
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.weightedListHeightProvider(
	block: WeightedListHeightProviderBuilder.() -> Unit,
) = weightedListHeightProvider(WeightedListHeightProviderBuilder().apply(block).entries)
