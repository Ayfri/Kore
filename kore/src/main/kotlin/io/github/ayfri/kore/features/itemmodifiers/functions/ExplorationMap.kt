package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.enums.MapDecoration
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.Tags
import kotlinx.serialization.Serializable

@Serializable
data class ExplorationMap(
	override var conditions: PredicateAsList? = null,
	var destination: Tags.Worldgen.Structure? = null,
	var decoration: MapDecoration? = null,
	var zoom: Int? = null,
	var searchRadius: Int? = null,
	var skipExistingChunks: Boolean? = null,
) : ItemFunction()

fun ItemModifier.explorationMap(
	destination: Tags.Worldgen.Structure,
	decoration: MapDecoration? = null,
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
