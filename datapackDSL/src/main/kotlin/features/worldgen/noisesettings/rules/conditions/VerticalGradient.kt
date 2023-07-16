package features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

@Serializable
data class VerticalGradient(
	var randomName: String,
	var trueAtAndBelow: GradientComparison,
	var falseAtAndAbove: GradientComparison
) : SurfaceRuleCondition()

fun verticalGradient(
	randomName: String,
	trueAtAndBelow: GradientComparison,
	falseAtAndAbove: GradientComparison
) = VerticalGradient(randomName, trueAtAndBelow, falseAtAndAbove)
