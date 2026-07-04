package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import kotlinx.serialization.Serializable

@Serializable
data class WeightedPoolEntry(
	var weight: Int,
	var data: TemplatePoolArgument,
)

@Serializable
data class WeightedGroupEntry(
	var weight: Int,
	var data: List<PoolAlias>,
)
