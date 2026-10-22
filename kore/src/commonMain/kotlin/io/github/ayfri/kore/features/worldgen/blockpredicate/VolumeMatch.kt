package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Passes when every block of the box between the [min] and [max] corners, relative to the tested position, matches
 * [match].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#volume_match
 *
 * @property match The predicate every block of the box has to pass.
 * @property max The `[X, Y, Z]` offset of the second corner of the box, each component between `-16` and `16`.
 * @property min The `[X, Y, Z]` offset of the first corner of the box, each component between `-16` and `16`.
 */
@Serializable
data class VolumeMatch(
	var match: BlockPredicate,
	var max: TripleAsArray<Int, Int, Int>,
	var min: TripleAsArray<Int, Int, Int>,
) : BlockPredicate()

/**
 * Creates a `volume_match` block predicate, passing when every block of the box between the [min] and [max] corner
 * offsets matches the predicate built in [block].
 *
 * Unlike the single block predicates, it has no `offset`: the box is already placed by its two corners.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { volumeMatch(Triple(-2, -2, -2), Triple(2, -1, 2)) { not { matchingBlockTag(Tags.Block.AIR) } } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#volume_match
 */
fun BlockPredicateScope.volumeMatch(
	min: Triple<Int, Int, Int>,
	max: Triple<Int, Int, Int>,
	block: BlockPredicatesScope.() -> Unit,
) = VolumeMatch(blockPredicate(block), max, min).also { addBlockPredicate(it) }
