package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import kotlinx.serialization.Serializable

@Serializable
data class Direct(
	var alias: TemplatePoolArgument,
	var target: TemplatePoolArgument,
) : PoolAlias()
