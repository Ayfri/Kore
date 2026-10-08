package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.MaterialRulesScope
import kotlinx.serialization.Serializable

/**
 * Represents a condition that is true when [invert] is false, and vice versa.
 *
 * @property invert The condition to negate.
 */
@Serializable
data class Not(
	var invert: MaterialCondition,
) : MaterialCondition()

/** Creates a [Not] condition negating [invert]. */
fun MaterialRulesScope.not(invert: MaterialCondition) = Not(invert)

/** Creates a [Not] condition negating the condition returned by [invert]. */
fun MaterialRulesScope.not(invert: MaterialRulesScope.() -> MaterialCondition) = Not(invert())
