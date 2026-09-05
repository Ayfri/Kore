package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Passes when no entity occupies the space of the block at [offset].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#unobstructed
 *
 * @property offset The `[X, Y, Z]` block offset to test at, `[0, 0, 0]` when `null`.
 */
@Serializable
data class Unobstructed(
	override var offset: TripleAsArray<Int, Int, Int>? = null,
) : BlockPredicate(), OffsetBlockPredicate

/**
 * Creates an `unobstructed` block predicate, passing when no entity occupies the space of the tested block.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { unobstructed() }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#unobstructed
 */
fun BlockPredicateScope.unobstructed(init: Unobstructed.() -> Unit = {}) =
	Unobstructed().apply(init).also { addBlockPredicate(it) }
