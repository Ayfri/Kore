package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Passes when the block at [offset] can be replaced by a placed block, such as air, water or tall grass.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#replaceable
 *
 * @property offset The `[X, Y, Z]` block offset to test at, each component between `-16` and `16`, `[0, 0, 0]` when `null`.
 */
@Serializable
data class Replaceable(
	override var offset: TripleAsArray<Int, Int, Int>? = null,
) : BlockPredicate(), OffsetBlockPredicate

/**
 * Creates a `replaceable` block predicate, passing when the tested block can be replaced by a placed block.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { replaceable { offset(0, 1, 0) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#replaceable
 */
fun BlockPredicateScope.replaceable(init: Replaceable.() -> Unit = {}) =
	Replaceable().apply(init).also { addBlockPredicate(it) }
