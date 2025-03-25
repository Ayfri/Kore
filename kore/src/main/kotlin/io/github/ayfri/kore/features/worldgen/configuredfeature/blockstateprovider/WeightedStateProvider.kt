package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import kotlinx.serialization.Serializable

@Serializable
data class WeightedStateProvider(
	var entries: List<WeightedStateProviderValue> = emptyList(),
) : BlockStateProvider()

@Serializable
data class WeightedStateProviderValue(
	var weight: Int = 1,
	var data: BlockStateProvider,
)

fun weightedStateProvider(entries: List<WeightedStateProviderValue> = emptyList()) = WeightedStateProvider(entries)
fun weightedStateProvider(vararg entries: WeightedStateProviderValue) = WeightedStateProvider(entries.toList())
fun weightedStateProvider(block: MutableList<WeightedStateProviderValue>.() -> Unit) = WeightedStateProvider(buildList(block))
