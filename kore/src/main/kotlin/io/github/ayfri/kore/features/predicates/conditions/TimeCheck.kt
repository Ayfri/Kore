package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.IntOrNumberProvidersRange
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.intRange
import io.github.ayfri.kore.features.predicates.providers.providersRange
import kotlinx.serialization.Serializable

@Serializable
data class TimeCheck(
	var value: IntOrNumberProvidersRange,
	var period: Int? = null,
) : PredicateCondition()

fun Predicate.timeCheck(value: IntOrNumberProvidersRange, period: Int? = null) {
	predicateConditions += TimeCheck(value, period)
}

fun Predicate.timeCheck(value: Int, period: Int? = null) {
	predicateConditions += TimeCheck(IntOrNumberProvidersRange(value), period)
}

fun Predicate.timeCheck(value: ClosedFloatingPointRange<Float>, period: Int? = null) {
	predicateConditions += TimeCheck(intRange(value), period)
}

fun Predicate.timeCheck(min: NumberProvider, max: NumberProvider, period: Int? = null) {
	predicateConditions += TimeCheck(providersRange(min, max), period)
}

fun Predicate.timeCheck(min: Float, max: NumberProvider, period: Int? = null) {
	predicateConditions += TimeCheck(providersRange(min, max), period)
}

fun Predicate.timeCheck(min: NumberProvider, max: Float, period: Int? = null) {
	predicateConditions += TimeCheck(providersRange(min, max), period)
}

fun Predicate.timeCheck(min: Float, max: Float, period: Int? = null) {
	predicateConditions += TimeCheck(intRange(min, max), period)
}
