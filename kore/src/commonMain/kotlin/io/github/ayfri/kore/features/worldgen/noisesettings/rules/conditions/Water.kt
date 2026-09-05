package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.SurfaceRulesScope
import kotlinx.serialization.Serializable

/**
 * Represents a condition that checks whether the current position is at or below the water level, adjusted by
 * [offset], [surfaceDepthMultiplier], and optionally [addStoneDepth].
 *
 * @property offset The height offset applied to the water level before comparing.
 * @property surfaceDepthMultiplier The surface depth multiplier applied to the comparison.
 * @property addStoneDepth Whether the stone depth is added to the comparison.
 */
@Serializable
data class Water(
	var offset: Int = 0,
	var surfaceDepthMultiplier: Int = 0,
	var addStoneDepth: Boolean = false,
) : SurfaceRuleCondition()

/** Creates a [Water] condition with the given parameters. */
fun SurfaceRulesScope.water(
	offset: Int = 0,
	surfaceDepthMultiplier: Int = 0,
	addStoneDepth: Boolean = false,
) = Water(offset, surfaceDepthMultiplier, addStoneDepth)

/** Creates a [Water] condition, further configured in [block]. */
fun SurfaceRulesScope.water(
	block: Water.() -> Unit,
) = Water().apply(block)
