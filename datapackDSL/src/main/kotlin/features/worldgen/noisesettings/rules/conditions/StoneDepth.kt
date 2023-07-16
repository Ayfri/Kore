package features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable
data class StoneDepth(
	var offset: Double = 0.0,
	var surface: Surface,
	var addSurfaceDepth: Boolean = false,
	var secondaryDepthRange: Int = 0,
) : SurfaceRuleCondition()

@Serializable(with = Surface.Companion.SurfaceSerializer::class)
enum class Surface {
	CEILING,
	FLOOR;

	companion object {
		data object SurfaceSerializer : LowercaseSerializer<Surface>(entries)
	}
}

fun stoneDepth(
	surface: Surface,
	offset: Double = 0.0,
	addSurfaceDepth: Boolean = false,
	secondaryDepthRange: Int = 0,
) = StoneDepth(offset, surface, addSurfaceDepth, secondaryDepthRange)

fun stoneDepth(
	surface: Surface,
	block: StoneDepth.() -> Unit,
) = StoneDepth(surface = surface).apply(block)
