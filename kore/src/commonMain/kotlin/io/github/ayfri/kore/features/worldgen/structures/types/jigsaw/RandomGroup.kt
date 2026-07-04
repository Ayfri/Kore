package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import kotlinx.serialization.Serializable

@Serializable
data class RandomGroup(
	var groups: List<WeightedGroupEntry> = emptyList(),
) : PoolAlias()
