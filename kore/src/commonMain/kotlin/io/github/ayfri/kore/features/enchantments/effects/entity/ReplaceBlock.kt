package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.generated.arguments.types.GameEventArgument
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class ReplaceBlock(
	var blockState: BlockStateProvider,
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var predicate: BlockPredicate? = null,
	var triggerGameEvent: GameEventArgument? = null,
) : EntityEffect(), BlockPredicateScope

fun ReplaceBlock.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}

/**
 * Sets [ReplaceBlock.predicate] to the predicate built in [block], the condition the replaced block has to pass.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * replaceBlock(simpleStateProvider(Blocks.WATER)) {
 *     predicate { matchingBlocks(Blocks.LAVA) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun ReplaceBlock.predicate(block: BlockPredicatesScope.() -> Unit) {
	predicate = blockPredicate(block)
}
