package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

/**
 * Passes when [term] does not pass.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - inverted](https://minecraft.wiki/w/Predicate#inverted)
 */
@Serializable
data class Inverted(
	var term: PredicateCondition,
) : PredicateCondition()

/** Adds an [Inverted] condition negating the single condition declared in [term]. */
fun Predicate.inverted(term: Predicate.() -> Unit) {
	val conditions = Predicate().apply(term).predicateConditions
	require(conditions.size == 1) { "'inverted' takes exactly one condition, got ${conditions.size}." }
	predicateConditions += Inverted(conditions.first())
}

/** Adds an [Inverted] condition negating [term]. */
fun Predicate.inverted(term: PredicateCondition) {
	predicateConditions += Inverted(term)
}
