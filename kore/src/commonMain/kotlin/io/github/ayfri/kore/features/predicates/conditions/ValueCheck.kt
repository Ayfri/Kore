package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.*
import kotlinx.serialization.Serializable

@Serializable
data class ValueCheck(
	var value: NumberProvider,
	var range: IntOrNumberProvidersRange? = null,
) : PredicateCondition()

fun Predicate.valueCheck(value: NumberProvider, range: IntOrNumberProvidersRange? = null) {
	predicateConditions += ValueCheck(value, range)
}

fun Predicate.valueCheck(value: NumberProvider, range: ClosedFloatingPointRange<Float>) {
	predicateConditions += ValueCheck(value, intRange(range))
}

fun Predicate.valueCheck(value: NumberProvider, min: NumberProvider, max: NumberProvider) {
	predicateConditions += ValueCheck(value, providersRange(min, max))
}

fun Predicate.valueCheck(value: NumberProvider, min: Float, max: NumberProvider) {
	predicateConditions += ValueCheck(value, providersRange(min, max))
}

fun Predicate.valueCheck(value: NumberProvider, min: NumberProvider, max: Float) {
	predicateConditions += ValueCheck(value, providersRange(min, max))
}

fun Predicate.valueCheck(value: NumberProvider, min: Float, max: Float) {
	predicateConditions += ValueCheck(value, intRange(min, max))
}

fun Predicate.valueCheck(value: Float, range: IntOrNumberProvidersRange? = null) {
	predicateConditions += ValueCheck(constant(value), range)
}
