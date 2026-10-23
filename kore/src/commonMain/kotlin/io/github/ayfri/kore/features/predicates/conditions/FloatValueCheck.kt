package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProviderRange
import io.github.ayfri.kore.features.predicates.providers.floatRange
import kotlinx.serialization.Serializable

/**
 * Passes when the float produced by [value] falls within [test].
 *
 * [value] and the bounds of [test] can be context float provider IDs through `reference(...)`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - float_value_check](https://minecraft.wiki/w/Predicate#float_value_check)
 */
@Serializable
data class FloatValueCheck(
	var test: ContextFloatProviderRange,
	var value: ContextFloatProvider,
) : PredicateCondition()

/**
 * Adds a [FloatValueCheck] condition passing when [value] falls within [test].
 *
 * ```
 * floatValueCheck(constant(0.5f), floatValue(0.5f))  // "test": 0.5
 * ```
 */
fun Predicate.floatValueCheck(value: ContextFloatProvider, test: ContextFloatProviderRange) {
	predicateConditions += FloatValueCheck(test, value)
}

/**
 * Adds a [FloatValueCheck] condition passing when [value] falls within [range], both bounds included.
 *
 * ```
 * floatValueCheck(enchantmentLevel(1), 0f..1f)  // "test": { "max": 1.0, "min": 0.0 }
 * ```
 */
fun Predicate.floatValueCheck(value: ContextFloatProvider, range: ClosedFloatingPointRange<Float>) =
	floatValueCheck(value, floatRange(range))

/** Adds a [FloatValueCheck] condition passing when [value] falls between [min] and [max], both included, a `null` bound leaving that side open. */
fun Predicate.floatValueCheck(value: ContextFloatProvider, min: ContextFloatProvider? = null, max: ContextFloatProvider? = null) =
	floatValueCheck(value, floatRange(min, max))
