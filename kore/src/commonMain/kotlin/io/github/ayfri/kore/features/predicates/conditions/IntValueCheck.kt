package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProviderRange
import io.github.ayfri.kore.features.predicates.providers.intRange
import kotlinx.serialization.Serializable

/**
 * Passes when the integer produced by [value] falls within [test].
 *
 * [value] and the bounds of [test] can be context int provider IDs through `reference(...)`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - int_value_check](https://minecraft.wiki/w/Predicate#int_value_check)
 */
@Serializable
data class IntValueCheck(
	var test: ContextIntProviderRange,
	var value: ContextIntProvider,
) : PredicateCondition()

/**
 * Adds an [IntValueCheck] condition passing when [value] falls within [test].
 *
 * ```
 * intValueCheck(score("kills", EntityTarget.THIS), intValue(3))  // "test": 3
 * ```
 */
fun Predicate.intValueCheck(value: ContextIntProvider, test: ContextIntProviderRange) {
	predicateConditions += IntValueCheck(test, value)
}

/**
 * Adds an [IntValueCheck] condition passing when [value] falls within [range], both bounds included, the range being
 * matched and never drawn from.
 *
 * ```
 * intValueCheck(score("kills", EntityTarget.THIS), 0..10)  // "test": { "max": 10, "min": 0 }
 * ```
 */
fun Predicate.intValueCheck(value: ContextIntProvider, range: IntRange) = intValueCheck(value, intRange(range))

/** Adds an [IntValueCheck] condition passing when [value] falls between [min] and [max], both included, a `null` bound leaving that side open. */
fun Predicate.intValueCheck(value: ContextIntProvider, min: ContextIntProvider? = null, max: ContextIntProvider? = null) =
	intValueCheck(value, intRange(min, max))
