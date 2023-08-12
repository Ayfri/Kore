package features.worldgen.processorlist.types.rule.positionpredicate

import arguments.enums.Axis
import kotlinx.serialization.Serializable

@Serializable
data class AxisAlignedLinearPos(
	var axis: Axis,
	var minDist: Int? = null,
	var maxDist: Int? = null,
	var minChance: Double? = null,
	var maxChance: Double? = null,
) : PositionPredicate()

fun axisAlignedLinearPos(
	axis: Axis,
	block: AxisAlignedLinearPos.() -> Unit = {},
) = AxisAlignedLinearPos(axis).apply(block)
