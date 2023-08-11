package features.predicates.conditions

import features.advancements.types.DamageSource
import features.advancements.types.damageSource
import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class DamageSourceProperties(
	var predicate: DamageSource,
) : PredicateCondition()

fun Predicate.damageSourceProperties(block: DamageSource.() -> Unit = {}) {
	predicateConditions += DamageSourceProperties(damageSource(block))
}
