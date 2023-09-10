package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class KilledByPlayer(
	var inverse: Boolean? = null,
) : PredicateCondition()

fun Predicate.killedByPlayer(inverse: Boolean? = null) {
	predicateConditions += KilledByPlayer(inverse)
}
