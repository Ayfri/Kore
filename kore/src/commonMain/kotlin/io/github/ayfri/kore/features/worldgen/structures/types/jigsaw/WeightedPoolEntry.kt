package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import kotlinx.serialization.Serializable

/**
 * One template pool candidate of a [Random] alias.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property weight The relative chance this pool is the one drawn, against the other entries of the alias.
 * @property data The template pool the alias resolves to when this entry is drawn.
 */
@Serializable
data class WeightedPoolEntry(
	var weight: Int,
	var data: TemplatePoolArgument,
)

/**
 * One alias group candidate of a [RandomGroup] alias.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property weight The relative chance this group is the one drawn, against the other groups of the alias.
 * @property data The aliases applied together when this entry is drawn.
 */
@Serializable
data class WeightedGroupEntry(
	var weight: Int,
	var data: List<PoolAlias>,
)
