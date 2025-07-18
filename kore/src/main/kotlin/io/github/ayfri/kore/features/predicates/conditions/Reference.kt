package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class Reference(
	var name: String,
) : PredicateCondition()

fun Predicate.reference(name: String) {
	predicateConditions += Reference(name)
}

context(dp: DataPack)
fun Predicate.reference(predicate: Predicate) {
	predicateConditions += Reference("${dp.name}/${predicate.fileName}")
}
