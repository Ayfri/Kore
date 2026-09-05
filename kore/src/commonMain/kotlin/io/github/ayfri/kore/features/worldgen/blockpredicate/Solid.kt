package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Passes when the block at [offset] is solid.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#solid
 *
 * @property offset The `[X, Y, Z]` block offset to test at, each component between `-16` and `16`, `[0, 0, 0]` when `null`.
 */
@Serializable
data class Solid(
	override var offset: TripleAsArray<Int, Int, Int>? = null,
) : BlockPredicate(), OffsetBlockPredicate

/**
 * Creates a `solid` block predicate, passing when the tested block is solid.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { solid { offset(0, -1, 0) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#solid
 */
fun BlockPredicateScope.solid(init: Solid.() -> Unit = {}) = Solid().apply(init).also { addBlockPredicate(it) }
