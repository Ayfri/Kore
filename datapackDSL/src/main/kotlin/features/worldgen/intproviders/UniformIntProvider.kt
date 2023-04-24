package features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:uniform")
data class UniformIntProvider(
	var minInclusive: Int,
	var maxInclusive: Int,
) : IntProvider
