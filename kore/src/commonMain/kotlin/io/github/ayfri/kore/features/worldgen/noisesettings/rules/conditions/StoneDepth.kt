package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.SurfaceRulesScope
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Represents a condition that checks the depth of stone (or another equivalent block) at the current position.
 *
 * @property offset The depth offset to apply before comparing.
 * @property surfaceType Whether the depth is measured from the [Surface.CEILING] or the [Surface.FLOOR].
 * @property addSurfaceDepth Whether the surface depth is added to the comparison.
 * @property secondaryDepthRange The range of the secondary depth noise added to the comparison.
 */
@Serializable
data class StoneDepth(
	var offset: Double = 0.0,
	var surfaceType: Surface,
	var addSurfaceDepth: Boolean = false,
	var secondaryDepthRange: Int = 0,
) : SurfaceRuleCondition()

/**
 * The side of the terrain a [StoneDepth] condition measures from.
 */
@Serializable(with = Surface.Companion.SurfaceSerializer::class)
enum class Surface {
	CEILING,
	FLOOR;

	companion object {
		data object SurfaceSerializer : LowercaseSerializer<Surface>(entries)
	}
}

/** Creates a [StoneDepth] condition with the given parameters. */
fun SurfaceRulesScope.stoneDepth(
	surfaceType: Surface,
	offset: Double = 0.0,
	addSurfaceDepth: Boolean = false,
	secondaryDepthRange: Int = 0,
) = StoneDepth(offset, surfaceType, addSurfaceDepth, secondaryDepthRange)

/** Creates a [StoneDepth] condition for [surfaceType], further configured in [block]. */
fun SurfaceRulesScope.stoneDepth(
	surfaceType: Surface,
	block: StoneDepth.() -> Unit,
) = StoneDepth(surfaceType = surfaceType).apply(block)
