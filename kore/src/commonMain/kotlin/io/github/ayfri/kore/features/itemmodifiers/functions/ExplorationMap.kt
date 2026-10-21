package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.generated.arguments.types.MapDecorationTypeArgument
import io.github.ayfri.kore.generated.arguments.worldgen.ConfiguredStructureOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Converts an empty map into an explorer map leading to the nearest of the [destination] structures, written as a
 * single structure ID, a `#tag` or a list of IDs. Mirrors vanilla `minecraft:exploration_map`.
 *
 * ```
 * explorationMap(Tags.Worldgen.Structure.ON_TREASURE_MAPS)  // "destination": "#minecraft:on_treasure_maps"
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 */
@Serializable
data class ExplorationMap(
	override var condition: PredicateCondition? = null,
	var decoration: MapDecorationTypeArgument? = null,
	var destination: InlinableList<ConfiguredStructureOrTagArgument>,
	var searchRadius: Int? = null,
	var skipExistingChunks: Boolean? = null,
	var zoom: Int? = null,
) : ItemFunction()

/**
 * Add an `exploration_map` step leading to one of the [destination] structures or structure tags.
 *
 * ```
 * explorationMap(ConfiguredStructures.MONUMENT, ConfiguredStructures.MANSION, decoration = MapDecorationTypes.RED_X)
 * ```
 */
fun ItemModifier.explorationMap(
	vararg destination: ConfiguredStructureOrTagArgument,
	decoration: MapDecorationTypeArgument? = null,
	zoom: Int? = null,
	searchRadius: Int? = null,
	skipExistingChunks: Boolean? = null,
	block: ExplorationMap.() -> Unit = {},
) {
	modifiers += ExplorationMap(
		decoration = decoration,
		destination = destination.toList(),
		searchRadius = searchRadius,
		skipExistingChunks = skipExistingChunks,
		zoom = zoom,
	).apply(block)
}
