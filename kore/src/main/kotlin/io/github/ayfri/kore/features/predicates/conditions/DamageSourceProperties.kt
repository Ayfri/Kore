package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.DamageSource
import kotlinx.serialization.Serializable

@Serializable
data class DamageSourceProperties(
	var predicate: DamageSource,
) : PredicateCondition()

fun Predicate.damageSourceProperties(block: DamageSource.() -> Unit = {}) {
	predicateConditions += DamageSourceProperties(DamageSource().apply(block))
}
