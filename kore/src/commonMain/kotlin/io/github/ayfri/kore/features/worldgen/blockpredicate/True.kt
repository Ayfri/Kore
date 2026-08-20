package io.github.ayfri.kore.features.worldgen.blockpredicate

import kotlinx.serialization.Serializable

/**
 * Passes for every block, whatever its state is.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#true
 */
@Serializable
data object True : BlockPredicate()

/**
 * Creates a `true` block predicate, passing for every block.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { alwaysTrue() }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#true
 */
fun BlockPredicateScope.alwaysTrue() = True.also { addBlockPredicate(it) }
