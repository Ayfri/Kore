package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Passes when the Y level of the tested block is between [minInclusive] and [maxInclusive], both included.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#height_range
 *
 * @property maxInclusive The highest Y level that passes.
 * @property minInclusive The lowest Y level that passes.
 */
@Serializable
@SerialName("height_range")
data class HeightRangeBlockPredicate(
	var maxInclusive: VerticalAnchor,
	var minInclusive: VerticalAnchor,
) : BlockPredicate()

/**
 * Creates a `height_range` block predicate, passing when the Y level of the tested block is between [minInclusive]
 * and [maxInclusive], both included.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { heightRange(aboveBottom(8), absolute(64)) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#height_range
 */
fun BlockPredicateScope.heightRange(minInclusive: VerticalAnchor, maxInclusive: VerticalAnchor) =
	HeightRangeBlockPredicate(maxInclusive, minInclusive).also { addBlockPredicate(it) }
