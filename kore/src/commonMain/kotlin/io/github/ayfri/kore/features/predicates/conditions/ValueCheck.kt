package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.*
import kotlinx.serialization.Serializable

/**
 * Passes when the number produced by [value] falls within [range].
 *
 * Both bounds are clamped to an integer by the game.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - value_check](https://minecraft.wiki/w/Predicate#value_check)
 */
@Serializable
data class ValueCheck(
	var value: NumberProvider,
	var range: IntOrNumberProvidersRange,
) : PredicateCondition()

/** Adds a [ValueCheck] condition passing when [value] falls within [range]. */
fun Predicate.valueCheck(value: NumberProvider, range: IntOrNumberProvidersRange) {
	predicateConditions += ValueCheck(value, range)
}

/** Adds a [ValueCheck] condition passing when [value] falls within [range]. */
fun Predicate.valueCheck(value: NumberProvider, range: ClosedFloatingPointRange<Float>) {
	predicateConditions += ValueCheck(value, intRange(range))
}

/** Adds a [ValueCheck] condition passing when [value] falls within [range]. */
fun Predicate.valueCheck(value: NumberProvider, range: IntRange) {
	predicateConditions += ValueCheck(value, intRange(range))
}

/** Adds a [ValueCheck] condition passing when [value] falls between [min] and [max], both included. */
fun Predicate.valueCheck(value: NumberProvider, min: NumberProvider, max: NumberProvider) {
	predicateConditions += ValueCheck(value, intRange(min, max))
}

/** Adds a [ValueCheck] condition passing when [value] falls between [min] and [max], both included. */
fun Predicate.valueCheck(value: NumberProvider, min: Float, max: NumberProvider) {
	predicateConditions += ValueCheck(value, intRange(min, max))
}

/** Adds a [ValueCheck] condition passing when [value] falls between [min] and [max], both included. */
fun Predicate.valueCheck(value: NumberProvider, min: NumberProvider, max: Float) {
	predicateConditions += ValueCheck(value, intRange(min, max))
}

/** Adds a [ValueCheck] condition passing when [value] falls between [min] and [max], both included. */
fun Predicate.valueCheck(value: NumberProvider, min: Float, max: Float) {
	predicateConditions += ValueCheck(value, intRange(min, max))
}

/** Adds a [ValueCheck] condition passing when the fixed [value] falls within [range]. */
fun Predicate.valueCheck(value: Float, range: IntOrNumberProvidersRange) {
	predicateConditions += ValueCheck(constant(value), range)
}
