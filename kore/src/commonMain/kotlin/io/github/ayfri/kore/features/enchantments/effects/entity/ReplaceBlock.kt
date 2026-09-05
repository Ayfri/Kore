package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.generated.arguments.types.GameEventArgument
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Replaces the block at the target position by [blockState].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#replace_block
 *
 * @property blockState The block state placed instead of the existing one.
 * @property offset The `[X, Y, Z]` block offset applied to the target position, `[0, 0, 0]` when `null`.
 * @property predicate The condition the replaced block has to pass, always passing when `null`.
 * @property triggerGameEvent The game event emitted at the position, none when `null`.
 */
@Serializable
data class ReplaceBlock(
	var blockState: BlockStateProvider = SimpleStateProvider(),
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var predicate: BlockPredicate? = null,
	var triggerGameEvent: GameEventArgument? = null,
) : EntityEffect(), BlockPredicateScope, BlockStateProviderScope

/** Tests and replaces the block offset by [x], [y] and [z] from the target position. */
fun ReplaceBlock.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}

/**
 * Sets [ReplaceBlock.predicate] to the predicate built in [block], the condition the replaced block has to pass.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * replaceBlock {
 *     blockState = simpleStateProvider(Blocks.WATER)
 *     predicate { matchingBlocks(Blocks.LAVA) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun ReplaceBlock.predicate(block: BlockPredicatesScope.() -> Unit) {
	predicate = blockPredicate(block)
}
