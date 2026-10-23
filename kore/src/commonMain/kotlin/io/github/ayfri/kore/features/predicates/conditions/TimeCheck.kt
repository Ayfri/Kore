package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProviderRange
import io.github.ayfri.kore.features.predicates.providers.intRange
import io.github.ayfri.kore.features.predicates.providers.intValue
import io.github.ayfri.kore.generated.arguments.types.WorldClockArgument
import kotlinx.serialization.Serializable

/**
 * Passes when the total ticks of [clock], optionally divided modulo [period], fall within [value].
 *
 * The bounds of [value] can be context int provider IDs through `reference(...)`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - time_check](https://minecraft.wiki/w/Predicate#time_check)
 */
@Serializable
data class TimeCheck(
	var clock: WorldClockArgument,
	var value: ContextIntProviderRange,
	var period: Int? = null,
) : PredicateCondition()

/** Adds a [TimeCheck] condition with a [ContextIntProviderRange] [value]. */
fun Predicate.timeCheck(clock: WorldClockArgument, value: ContextIntProviderRange, period: Int? = null) {
	predicateConditions += TimeCheck(clock, value, period)
}

/**
 * Adds a [TimeCheck] condition matching the exact tick [value].
 *
 * ```
 * timeCheck(WorldClocks.OVERWORLD, 1000)  // "value": 1000
 * ```
 */
fun Predicate.timeCheck(clock: WorldClockArgument, value: Int, period: Int? = null) =
	timeCheck(clock, intValue(value), period)

/**
 * Adds a [TimeCheck] condition matching the tick [range], both bounds included.
 *
 * ```
 * timeCheck(WorldClocks.OVERWORLD, 0..12000, period = 24000)  // "value": { "max": 12000, "min": 0 }, "period": 24000
 * ```
 */
fun Predicate.timeCheck(clock: WorldClockArgument, range: IntRange, period: Int? = null) =
	timeCheck(clock, intRange(range), period)

/** Adds a [TimeCheck] condition with the [ContextIntProvider] bounds [min] and [max], a `null` bound leaving that side open. */
fun Predicate.timeCheck(clock: WorldClockArgument, min: ContextIntProvider?, max: ContextIntProvider?, period: Int? = null) =
	timeCheck(clock, intRange(min, max), period)
