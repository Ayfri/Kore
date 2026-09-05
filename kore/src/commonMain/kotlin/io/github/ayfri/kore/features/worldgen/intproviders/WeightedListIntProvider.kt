package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns an integer drawn from a weighted list of nested int providers, each entry being picked with a probability
 * of its weight over the total weight.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 *
 * @property distribution The entries to draw from, which must not be empty.
 */
@Serializable
@SerialName("minecraft:weighted_list")
data class WeightedListIntProvider(
	var distribution: List<WeightedListIntProviderEntry>,
) : IntProvider

/**
 * One entry of a [WeightedListIntProvider].
 *
 * @property weight Relative chance of drawing this entry, compared to the other entries of the list.
 * @property data The int provider used when this entry is drawn.
 */
@Serializable
data class WeightedListIntProviderEntry(
	var weight: Int,
	var data: IntProvider,
)

/** Creates a [WeightedListIntProviderEntry] drawing from [data] with a relative chance of [weight]. */
fun weightedListEntry(weight: Int, data: IntProvider) = WeightedListIntProviderEntry(weight, data)

/**
 * Builder scope for the entries of a [WeightedListIntProvider], adding [entry] on top of the int provider builders
 * inherited from [IntProviderScope].
 */
class WeightedListIntProviderBuilder internal constructor() : IntProviderScope {
	internal val entries = mutableListOf<WeightedListIntProviderEntry>()

	/** Adds an entry drawing from [data] with a relative chance of [weight]. */
	fun entry(weight: Int, data: IntProvider) {
		entries += WeightedListIntProviderEntry(weight, data)
	}
}
