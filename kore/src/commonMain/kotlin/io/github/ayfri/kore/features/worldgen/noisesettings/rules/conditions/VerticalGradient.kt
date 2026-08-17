package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.SurfaceRulesScope
import kotlinx.serialization.Serializable

@Serializable
data class VerticalGradient(
	var randomName: String,
	var trueAtAndBelow: HeightConstant,
	var falseAtAndAbove: HeightConstant,
) : SurfaceRuleCondition()

fun SurfaceRulesScope.verticalGradient(
	randomName: String,
	trueAtAndBelow: HeightConstant,
	falseAtAndAbove: HeightConstant,
) = VerticalGradient(randomName, trueAtAndBelow, falseAtAndAbove)
