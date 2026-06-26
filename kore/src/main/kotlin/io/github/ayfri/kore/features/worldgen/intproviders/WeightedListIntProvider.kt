package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Int provider that randomly selects from a pool of [WeightedEntryIntProvider]s.
 *
 * Each entry's [WeightedEntryIntProvider.weight] controls its relative selection probability.
 */
@Serializable
@SerialName("minecraft:weighted_list")
data class WeightedListIntProvider(
	val distribution: MutableList<WeightedEntryIntProvider>
) : IntProvider

/** A single entry in a [WeightedListIntProvider], pairing [data] with a relative [weight]. */
@Serializable
data class WeightedEntryIntProvider(
	var weight: Int,
	var data: IntProvider
)
