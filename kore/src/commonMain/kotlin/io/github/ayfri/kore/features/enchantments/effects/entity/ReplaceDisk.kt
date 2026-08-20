package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
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
 * Replaces a horizontal disk of blocks around the target position by [blockState], as the Frost Walker enchantment
 * does with the water it freezes.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#replace_disk
 *
 * @property blockState The block state placed instead of the existing ones.
 * @property radius The horizontal radius of the disk, per enchantment level.
 * @property height The amount of blocks the disk is made of vertically, per enchantment level.
 * @property offset The `[X, Y, Z]` block offset applied to the center of the disk, `[0, 0, 0]` when `null`.
 * @property predicate The condition the replaced blocks have to pass, always passing when `null`.
 * @property triggerGameEvent The game event emitted at the position, none when `null`.
 */
@Serializable
data class ReplaceDisk(
	var blockState: BlockStateProvider = SimpleStateProvider(),
	var radius: LevelBased = constantLevelBased(0),
	var height: LevelBased = constantLevelBased(0),
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var predicate: BlockPredicate? = null,
	var triggerGameEvent: GameEventArgument? = null,
) : EntityEffect(), BlockPredicateScope, BlockStateProviderScope

/** Sets [ReplaceDisk.radius] to a constant [value], whatever the enchantment level is. */
fun ReplaceDisk.radius(value: Int) {
	radius = constantLevelBased(value)
}

/** Sets [ReplaceDisk.height] to a constant [value], whatever the enchantment level is. */
fun ReplaceDisk.height(value: Int) {
	height = constantLevelBased(value)
}

/** Centers the disk on the block offset by [x], [y] and [z] from the target position. */
fun ReplaceDisk.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}

/**
 * Sets [ReplaceDisk.predicate] to the predicate built in [block], the condition the replaced blocks have to pass.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * replaceDisk {
 *     blockState = simpleStateProvider(Blocks.FROSTED_ICE)
 *     predicate { matchingBlocks(Blocks.WATER) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun ReplaceDisk.predicate(block: BlockPredicatesScope.() -> Unit) {
	predicate = blockPredicate(block)
}
