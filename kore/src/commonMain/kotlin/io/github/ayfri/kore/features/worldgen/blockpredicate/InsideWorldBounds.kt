package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Passes when the Y level of the position at [offset] is inside the height limits of the world.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#inside_world_bounds
 *
 * @property offset The `[X, Y, Z]` block offset to test at, each component between `-16` and `16`, `[0, 0, 0]` when `null`.
 */
@Serializable
data class InsideWorldBounds(
	override var offset: TripleAsArray<Int, Int, Int>? = null,
) : BlockPredicate(), OffsetBlockPredicate

/**
 * Creates an `inside_world_bounds` block predicate, passing when the tested position is inside the height limits of
 * the world.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { insideWorldBounds { offset(0, -3, 0) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#inside_world_bounds
 */
fun BlockPredicateScope.insideWorldBounds(init: InsideWorldBounds.() -> Unit = {}) =
	InsideWorldBounds().apply(init).also { addBlockPredicate(it) }
