package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.Location
import kotlinx.serialization.Serializable

@Serializable
data class LocationCheck(
	var offsetX: Int? = null,
	var offsetY: Int? = null,
	var offsetZ: Int? = null,
	var predicate: Location? = null,
) : PredicateCondition()

fun Predicate.locationCheck(offsetX: Int? = null, offsetY: Int? = null, offsetZ: Int? = null, predicate: Location? = null) {
	predicateConditions += LocationCheck(offsetX, offsetY, offsetZ, predicate)
}

fun Predicate.locationCheck(block: Location.() -> Unit = {}) {
	predicateConditions += LocationCheck(predicate = Location().apply(block))
}
