package features.worldgen.configuredfeature.blockstateprovider

import kotlinx.serialization.Serializable

@Serializable
data class WeightedStateProvider(
	var entries: List<BlockStateProvider> = emptyList(),
) : BlockStateProvider()

fun weightedStateProvider(entries: List<BlockStateProvider> = emptyList()) = WeightedStateProvider(entries)
fun weightedStateProvider(vararg entries: BlockStateProvider) = WeightedStateProvider(entries.toList())
