package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.arguments.types.resources.worldgen.TemplatePoolArgument
import kotlinx.serialization.Serializable

@Serializable
data class Direct(
	var alias: TemplatePoolArgument,
	var target: TemplatePoolArgument,
) : PoolAlias()
