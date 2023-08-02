package features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

@Serializable
data class VerticalGradient(
	var randomName: String,
	var trueAtAndBelow: HeightConstant,
	var falseAtAndAbove: HeightConstant,
) : SurfaceRuleCondition()

fun verticalGradient(
	randomName: String,
	trueAtAndBelow: HeightConstant,
	falseAtAndAbove: HeightConstant,
) = VerticalGradient(randomName, trueAtAndBelow, falseAtAndAbove)
