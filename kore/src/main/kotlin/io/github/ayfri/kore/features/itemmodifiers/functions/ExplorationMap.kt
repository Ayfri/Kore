package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.arguments.types.MapDecorationTypeArgument
import kotlinx.serialization.Serializable

/**
 * Converts an empty map into an explorer map leading to a nearby structure. Mirrors
 * vanilla `minecraft:exploration_map`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 */
@Serializable
data class ExplorationMap(
	override var conditions: PredicateAsList? = null,
	@Serializable(TaggedResourceLocationArgument.TaggedResourceLocationUnPrefixedSerializer::class)
	var destination: Tags.Worldgen.Structure? = null,
	var decoration: MapDecorationTypeArgument? = null,
	var zoom: Int? = null,
	var searchRadius: Int? = null,
	var skipExistingChunks: Boolean? = null,
) : ItemFunction()

fun ItemModifier.explorationMap(
	destination: Tags.Worldgen.Structure,
	decoration: MapDecorationTypeArgument? = null,
	zoom: Int? = null,
	searchRadius: Int? = null,
	skipExistingChunks: Boolean? = null,
	block: ExplorationMap.() -> Unit = {},
) {
	modifiers += ExplorationMap(
		destination = destination,
		decoration = decoration,
		zoom = zoom,
		searchRadius = searchRadius,
		skipExistingChunks = skipExistingChunks
	).apply(block)
}

fun ItemModifier.explorationMap(block: ExplorationMap.() -> Unit) {
	modifiers += ExplorationMap().apply(block)
}
