package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

/**
 * Passes with a probability of `1 / explosion radius`, or always when the loot context has no explosion radius.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - survives_explosion](https://minecraft.wiki/w/Predicate#survives_explosion)
 */
@Serializable
data object SurvivesExplosion : PredicateCondition()

/** Adds a [SurvivesExplosion] condition. */
fun Predicate.survivesExplosion() {
	predicateConditions += SurvivesExplosion
}
