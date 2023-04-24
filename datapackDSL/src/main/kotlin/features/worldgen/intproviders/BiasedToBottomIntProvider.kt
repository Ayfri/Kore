package features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:biased_to_bottom")
data class BiasedToBottomIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
) : IntProvider
