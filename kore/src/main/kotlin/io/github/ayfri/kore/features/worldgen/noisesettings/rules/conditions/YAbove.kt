package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

/**
 * Represents a condition that determines whether a surface rule should be executed.
 * YAbove is a condition that determines whether the Y value is above a certain anchor point.
 *
 * @property anchor The anchor point for the gradient comparison.
 * @property surfaceDepthMultiplier The depth multiplier for the surface.
 * @property addStoneDepth Specifies whether to add stone depth or not.
 */
@Serializable
data class YAbove(
	var anchor: HeightConstant,
	var surfaceDepthMultiplier: Int = 0,
	var addStoneDepth: Boolean = false,
) : SurfaceRuleCondition()

/**
 * Creates a [YAbove] instance with the given parameters.
 */
fun yAbove(
	anchor: HeightConstant,
	surfaceDepthMultiplier: Int = 0,
	addStoneDepth: Boolean = false,
) = YAbove(anchor, surfaceDepthMultiplier, addStoneDepth)

/**
 * Creates a [YAbove] instance with the given parameters.
 */
fun yAbove(
	anchor: HeightConstant,
	block: YAbove.() -> Unit,
) = YAbove(anchor).apply(block)
