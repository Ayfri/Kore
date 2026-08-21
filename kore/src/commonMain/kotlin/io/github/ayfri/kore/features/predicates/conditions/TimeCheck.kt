package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.IntOrNumberProvidersRange
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.intRange
import io.github.ayfri.kore.features.predicates.providers.providersRange
import io.github.ayfri.kore.generated.arguments.types.WorldClockArgument
import kotlinx.serialization.Serializable

/**
 * Predicate condition that checks the time of a world clock.
 *
 * Passes when the total ticks of [clock] (optionally divided modulo [period]) fall within [value].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Predicate#time_check
 */
@Serializable
data class TimeCheck(
	var clock: WorldClockArgument,
	var value: IntOrNumberProvidersRange,
	var period: Int? = null,
) : PredicateCondition()

/** Adds a [TimeCheck] condition with an [IntOrNumberProvidersRange] [value]. */
fun Predicate.timeCheck(clock: WorldClockArgument, value: IntOrNumberProvidersRange, period: Int? = null) {
	predicateConditions += TimeCheck(clock, value, period)
}

/** Adds a [TimeCheck] condition matching the exact tick [value]. */
fun Predicate.timeCheck(clock: WorldClockArgument, value: Int, period: Int? = null) {
	predicateConditions += TimeCheck(clock, IntOrNumberProvidersRange(value), period)
}

/** Adds a [TimeCheck] condition matching a float range [value]. */
fun Predicate.timeCheck(clock: WorldClockArgument, value: ClosedFloatingPointRange<Float>, period: Int? = null) {
	predicateConditions += TimeCheck(clock, intRange(value), period)
}

/** Adds a [TimeCheck] condition with [NumberProvider] min and max bounds. */
fun Predicate.timeCheck(clock: WorldClockArgument, min: NumberProvider, max: NumberProvider, period: Int? = null) {
	predicateConditions += TimeCheck(clock, providersRange(min, max), period)
}

/** Adds a [TimeCheck] condition with a float [min] and [NumberProvider] [max]. */
fun Predicate.timeCheck(clock: WorldClockArgument, min: Float, max: NumberProvider, period: Int? = null) {
	predicateConditions += TimeCheck(clock, providersRange(min, max), period)
}

/** Adds a [TimeCheck] condition with a [NumberProvider] [min] and float [max]. */
fun Predicate.timeCheck(clock: WorldClockArgument, min: NumberProvider, max: Float, period: Int? = null) {
	predicateConditions += TimeCheck(clock, providersRange(min, max), period)
}

/** Adds a [TimeCheck] condition with float [min] and [max] bounds. */
fun Predicate.timeCheck(clock: WorldClockArgument, min: Float, max: Float, period: Int? = null) {
	predicateConditions += TimeCheck(clock, intRange(min, max), period)
}
