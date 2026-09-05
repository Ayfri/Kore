package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

/**
 * Passes when the tool used to break the block matches [predicate].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - match_tool](https://minecraft.wiki/w/Predicate#match_tool)
 */
@Serializable
data class MatchTool(
	var predicate: ItemStackPredicate,
) : PredicateCondition()

/** Adds a [MatchTool] condition passing when the tool is any of [items]. */
fun Predicate.matchTool(vararg items: ItemOrTagArgument) {
	predicateConditions += MatchTool(ItemStackPredicate(items = items.toList()))
}

/** Adds a [MatchTool] condition matching [predicate]. */
fun Predicate.matchTool(predicate: ItemStackPredicate) {
	predicateConditions += MatchTool(predicate)
}

/** Adds a [MatchTool] condition matching the item built by [block]. */
fun Predicate.matchTool(block: ItemStackPredicate.() -> Unit) {
	predicateConditions += MatchTool(ItemStackPredicate().apply(block))
}
