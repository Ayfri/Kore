package features.worldgen.heightproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:weighted_list")
data class WeightedListHeightProvider(
	var distribution: List<WeightedListHeightProviderEntry>,
) : HeightProvider()

@Serializable
data class WeightedListHeightProviderEntry(
	var weight: Int,
	var data: HeightProvider,
)

fun weightedListHeightProvider(
	distribution: List<WeightedListHeightProviderEntry>,
) = WeightedListHeightProvider(distribution)

fun weightedListHeightProvider(
	vararg distribution: Pair<Int, HeightProvider>,
) = weightedListHeightProvider(distribution.map { WeightedListHeightProviderEntry(it.first, it.second) })

fun weightedListHeightProvider(
	block: MutableList<WeightedListHeightProviderEntry>.() -> Unit,
) = weightedListHeightProvider(buildList(block))
