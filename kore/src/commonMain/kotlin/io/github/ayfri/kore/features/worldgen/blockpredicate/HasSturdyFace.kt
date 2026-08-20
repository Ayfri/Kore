package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Passes when the block at [offset] has a full supporting surface on its [direction] face.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#has_sturdy_face
 *
 * @property offset The `[X, Y, Z]` block offset to test at, each component between `-16` and `16`, `[0, 0, 0]` when `null`.
 * @property direction The face that has to be sturdy.
 */
@Serializable
data class HasSturdyFace(
	override var offset: TripleAsArray<Int, Int, Int>? = null,
	var direction: Direction,
) : BlockPredicate(), OffsetBlockPredicate

/**
 * Creates a `has_sturdy_face` block predicate, passing when the tested block has a full supporting surface on its
 * [direction] face.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { hasSturdyFace(Direction.UP) { offset(0, -1, 0) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#has_sturdy_face
 */
fun BlockPredicateScope.hasSturdyFace(direction: Direction, init: HasSturdyFace.() -> Unit = {}) =
	HasSturdyFace(direction = direction).apply(init).also { addBlockPredicate(it) }
