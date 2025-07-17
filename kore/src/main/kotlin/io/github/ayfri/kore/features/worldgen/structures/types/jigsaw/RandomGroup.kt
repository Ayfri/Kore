package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import kotlinx.serialization.Serializable

@Serializable
data class RandomGroup(
	var alias: TemplatePoolArgument,
	var groups: List<PoolAlias> = emptyList(),
) : PoolAlias()
