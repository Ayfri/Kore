package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.SurfaceRulesScope
import kotlinx.serialization.Serializable

/**
 * Represents a condition that is true when [invert] is false, and vice versa.
 *
 * @property invert The condition to negate.
 */
@Serializable
data class Not(
	var invert: SurfaceRuleCondition,
) : SurfaceRuleCondition()

/** Creates a [Not] condition negating [invert]. */
fun SurfaceRulesScope.not(invert: SurfaceRuleCondition) = Not(invert)

/** Creates a [Not] condition negating the condition returned by [invert]. */
fun SurfaceRulesScope.not(invert: SurfaceRulesScope.() -> SurfaceRuleCondition) = Not(invert())
