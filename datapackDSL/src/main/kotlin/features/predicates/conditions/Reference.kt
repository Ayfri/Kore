package features.predicates.conditions

import DataPack
import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class Reference(
	var name: String,
) : PredicateCondition

fun Predicate.reference(name: String) {
	predicateConditions += Reference(name)
}

context(DataPack)
fun Predicate.reference(predicate: Predicate) {
	predicateConditions += Reference("${this@DataPack.name}/${predicate.fileName}")
}
