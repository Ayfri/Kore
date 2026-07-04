package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.IntOrNumberProvidersRange
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.intRange
import io.github.ayfri.kore.features.predicates.providers.providersRange
import io.github.ayfri.kore.generated.arguments.types.WorldClockArgument
import kotlinx.serialization.Serializable

/**
 * Predicate condition that checks the current world time.
 *
 * Passes when the world time (optionally divided by [period]) falls within [value].
 * An optional [clock] selects which world clock to read; defaults to the day clock.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Predicate#time_check
 */
@Serializable
data class TimeCheck(
	var value: IntOrNumberProvidersRange,
	var period: Int? = null,
	var clock: WorldClockArgument? = null,
) : PredicateCondition()

/** Adds a [TimeCheck] condition with an [IntOrNumberProvidersRange] [value]. */
fun Predicate.timeCheck(value: IntOrNumberProvidersRange, period: Int? = null, clock: WorldClockArgument? = null) {
	predicateConditions += TimeCheck(value, period, clock)
}

/** Adds a [TimeCheck] condition matching the exact tick [value]. */
fun Predicate.timeCheck(value: Int, period: Int? = null, clock: WorldClockArgument? = null) {
	predicateConditions += TimeCheck(IntOrNumberProvidersRange(value), period, clock)
}

/** Adds a [TimeCheck] condition matching a float range [value]. */
fun Predicate.timeCheck(
	value: ClosedFloatingPointRange<Float>,
	period: Int? = null,
	clock: WorldClockArgument? = null
) {
	predicateConditions += TimeCheck(intRange(value), period, clock)
}

/** Adds a [TimeCheck] condition with [NumberProvider] min and max bounds. */
fun Predicate.timeCheck(
	min: NumberProvider,
	max: NumberProvider,
	period: Int? = null,
	clock: WorldClockArgument? = null
) {
	predicateConditions += TimeCheck(providersRange(min, max), period, clock)
}

/** Adds a [TimeCheck] condition with a float [min] and [NumberProvider] [max]. */
fun Predicate.timeCheck(min: Float, max: NumberProvider, period: Int? = null, clock: WorldClockArgument? = null) {
	predicateConditions += TimeCheck(providersRange(min, max), period, clock)
}

/** Adds a [TimeCheck] condition with a [NumberProvider] [min] and float [max]. */
fun Predicate.timeCheck(min: NumberProvider, max: Float, period: Int? = null, clock: WorldClockArgument? = null) {
	predicateConditions += TimeCheck(providersRange(min, max), period, clock)
}

/** Adds a [TimeCheck] condition with float [min] and [max] bounds. */
fun Predicate.timeCheck(min: Float, max: Float, period: Int? = null, clock: WorldClockArgument? = null) {
	predicateConditions += TimeCheck(intRange(min, max), period, clock)
}
