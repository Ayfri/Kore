package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:weighted_list")
data class WeightedListIntProvider(
	val distribution: MutableList<WeightedEntryIntProvider>
) : IntProvider

@Serializable
data class WeightedEntryIntProvider(
	var weight: Int,
	var data: IntProvider
)
