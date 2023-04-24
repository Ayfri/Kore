package features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:clamped")
data class ClampedIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
	var source: IntProvider
) : IntProvider
