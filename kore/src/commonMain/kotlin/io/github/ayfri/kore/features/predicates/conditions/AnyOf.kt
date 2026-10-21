package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Passes when at least one condition in [terms] passes. A single term is written inline, without the array. A list
 * can't mix predicate IDs and inline conditions, the game rejects it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - any_of](https://minecraft.wiki/w/Predicate#any_of)
 */
@Serializable
data class AnyOf(
	var terms: InlinableList<PredicateCondition> = emptyList(),
) : PredicateCondition()

/** Adds an [AnyOf] condition passing when at least one condition declared in [terms] passes. */
fun Predicate.anyOf(terms: Predicate.() -> Unit = {}) {
	predicateConditions += AnyOf(Predicate().apply(terms).predicateConditions)
}
