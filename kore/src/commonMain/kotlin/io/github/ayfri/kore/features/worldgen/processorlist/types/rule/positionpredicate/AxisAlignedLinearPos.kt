package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate

import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule
import kotlinx.serialization.Serializable

/**
 * Same as [LinearPos], but the distance is only measured along [axis] instead of in every direction.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property axis The axis the distance is measured along.
 * @property minDist The distance at which the chance is [minChance].
 * @property maxDist The distance at which the chance is [maxChance].
 * @property minChance The chance to pass at [minDist], between `0.0` and `1.0`.
 * @property maxChance The chance to pass at [maxDist], between `0.0` and `1.0`.
 */
@Serializable
data class AxisAlignedLinearPos(
	var axis: Axis = Axis.Y,
	var minDist: Int? = null,
	var maxDist: Int? = null,
	var minChance: Double? = null,
	var maxChance: Double? = null,
) : PositionPredicate()

/**
 * Creates an `axis_aligned_linear_pos` position predicate, passing with a chance based on the distance from the origin
 * of the structure piece along [axis].
 *
 * ```kotlin
 * rule {
 *     positionPredicate = axisAlignedLinearPos(Axis.Y) {
 *         minDist = 0
 *         maxDist = 4
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorRule.axisAlignedLinearPos(
	axis: Axis = Axis.Y,
	minDist: Int? = null,
	maxDist: Int? = null,
	minChance: Double? = null,
	maxChance: Double? = null,
	block: AxisAlignedLinearPos.() -> Unit = {},
) = AxisAlignedLinearPos(axis, minDist, maxDist, minChance, maxChance).apply(block)
