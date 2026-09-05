package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Passes when every condition in [terms] passes.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - all_of](https://minecraft.wiki/w/Predicate#all_of)
 */
@Serializable
data class AllOf(
	var terms: PredicateAsList = Predicate(),
) : PredicateCondition()

/** Adds an [AllOf] condition passing when every condition declared in [terms] passes. */
fun Predicate.allOf(terms: Predicate.() -> Unit = {}) {
	predicateConditions += AllOf(Predicate().apply(terms))
}
