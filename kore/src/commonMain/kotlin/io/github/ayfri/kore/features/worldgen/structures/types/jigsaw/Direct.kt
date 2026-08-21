package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import kotlinx.serialization.Serializable

/**
 * Always resolves [alias] to [target], whatever the structure instance is.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property alias The template pool referenced by the pieces of the structure.
 * @property target The template pool actually drawn from.
 */
@Serializable
data class Direct(
	var alias: TemplatePoolArgument,
	var target: TemplatePoolArgument,
) : PoolAlias()
