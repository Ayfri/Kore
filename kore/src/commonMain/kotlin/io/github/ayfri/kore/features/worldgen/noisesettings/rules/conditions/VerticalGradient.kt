package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.SurfaceRulesScope
import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor
import kotlinx.serialization.Serializable

/**
 * Represents a condition that randomly transitions between true and false over a vertical range, true at and below
 * [trueAtAndBelow], false at and above [falseAtAndAbove], and randomly one or the other in between.
 *
 * @property randomName The seed name used to randomize the transition.
 * @property trueAtAndBelow The height at and below which the condition is always true.
 * @property falseAtAndAbove The height at and above which the condition is always false.
 */
@Serializable
data class VerticalGradient(
	var randomName: String,
	var trueAtAndBelow: VerticalAnchor,
	var falseAtAndAbove: VerticalAnchor,
) : SurfaceRuleCondition()

/** Creates a [VerticalGradient] condition with the given parameters. */
fun SurfaceRulesScope.verticalGradient(
	randomName: String,
	trueAtAndBelow: VerticalAnchor,
	falseAtAndAbove: VerticalAnchor,
) = VerticalGradient(randomName, trueAtAndBelow, falseAtAndAbove)
