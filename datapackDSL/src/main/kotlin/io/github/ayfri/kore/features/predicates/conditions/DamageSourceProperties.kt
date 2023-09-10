package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.advancements.types.DamageSource
import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class DamageSourceProperties(
	var predicate: DamageSource,
) : PredicateCondition()

fun Predicate.damageSourceProperties(block: DamageSource.() -> Unit = {}) {
	predicateConditions += DamageSourceProperties(io.github.ayfri.kore.features.advancements.types.damageSource(block))
}
