package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate

import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule
import kotlinx.serialization.Serializable

/**
 * Passes with a chance interpolated between [minChance] and [maxChance], based on the distance from the origin of the
 * structure piece: [minDist] blocks away gives [minChance], [maxDist] blocks away gives [maxChance].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property minDist The distance at which the chance is [minChance].
 * @property maxDist The distance at which the chance is [maxChance].
 * @property minChance The chance to pass at [minDist], between `0.0` and `1.0`.
 * @property maxChance The chance to pass at [maxDist], between `0.0` and `1.0`.
 */
@Serializable
data class LinearPos(
	var minDist: Int? = null,
	var maxDist: Int? = null,
	var minChance: Double? = null,
	var maxChance: Double? = null,
) : PositionPredicate()

/**
 * Creates a `linear_pos` position predicate, passing with a chance based on the distance from the origin of the
 * structure piece.
 *
 * ```kotlin
 * rule {
 *     positionPredicate = linearPos(minDist = 0, maxDist = 8, minChance = 1.0, maxChance = 0.0)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorRule.linearPos(
	minDist: Int? = null,
	maxDist: Int? = null,
	minChance: Double? = null,
	maxChance: Double? = null,
	block: LinearPos.() -> Unit = {},
) = LinearPos(minDist, maxDist, minChance, maxChance).apply(block)
