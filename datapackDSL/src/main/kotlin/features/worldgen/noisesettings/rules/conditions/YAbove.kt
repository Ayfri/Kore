package features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

@Serializable
data class YAbove(
	var anchor: GradientComparison,
	var surfaceDepthMultiplier: Int = 0,
	var addStoneDepth: Boolean = false,
) : SurfaceRuleCondition()

fun yAbove(
	anchor: GradientComparison,
	surfaceDepthMultiplier: Int = 0,
	addStoneDepth: Boolean = false,
) = YAbove(anchor, surfaceDepthMultiplier, addStoneDepth)

fun yAbove(
	anchor: GradientComparison,
	block: YAbove.() -> Unit,
) = YAbove(anchor).apply(block)
