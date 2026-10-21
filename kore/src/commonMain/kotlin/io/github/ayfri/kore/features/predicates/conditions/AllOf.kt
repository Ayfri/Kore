package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Passes when every condition in [terms] passes. A single term is written inline, without the array. A list can't mix
 * predicate IDs and inline conditions, the game rejects it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - all_of](https://minecraft.wiki/w/Predicate#all_of)
 */
@Serializable
data class AllOf(
	var terms: InlinableList<PredicateCondition> = emptyList(),
) : PredicateCondition()

/** Adds an [AllOf] condition passing when every condition declared in [terms] passes. */
fun Predicate.allOf(terms: Predicate.() -> Unit = {}) {
	predicateConditions += AllOf(Predicate().apply(terms).predicateConditions)
}
