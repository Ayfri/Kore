package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import kotlinx.serialization.Serializable

/**
 * Resolves [alias] to one of [targets], drawn by weight once per structure instance.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property alias The template pool referenced by the pieces of the structure.
 * @property targets The weighted template pools one is drawn from.
 */
@Serializable
data class Random(
	var alias: TemplatePoolArgument,
	var targets: List<WeightedPoolEntry> = emptyList(),
) : PoolAlias()
