package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.DamageSourcePredicate
import kotlinx.serialization.Serializable

/**
 * Passes when the damage source of the loot context matches [predicate].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - damage_source_properties](https://minecraft.wiki/w/Predicate#damage_source_properties)
 */
@Serializable
data class DamageSourceProperties(
	var predicate: DamageSourcePredicate,
) : PredicateCondition()

/** Adds a [DamageSourceProperties] condition matching the damage source built by [block]. */
fun Predicate.damageSourceProperties(block: DamageSourcePredicate.() -> Unit = {}) {
	predicateConditions += DamageSourceProperties(DamageSourcePredicate().apply(block))
}
