package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.generated.arguments.types.GameEventArgument
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class ReplaceDisk(
	var blockState: BlockStateProvider,
	var radius: LevelBased = constantLevelBased(0),
	var height: LevelBased = constantLevelBased(0),
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var predicate: BlockPredicate? = null,
	var triggerGameEvent: GameEventArgument? = null,
) : EntityEffect(), BlockPredicateScope

fun ReplaceDisk.radius(value: Int) {
	radius = constantLevelBased(value)
}

fun ReplaceDisk.height(value: Int) {
	height = constantLevelBased(value)
}

fun ReplaceDisk.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}

/**
 * Sets [ReplaceDisk.predicate] to the predicate built in [block], the condition the replaced blocks have to pass.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * replaceDisk(simpleStateProvider(Blocks.FROSTED_ICE)) {
 *     predicate { matchingBlocks(Blocks.WATER) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun ReplaceDisk.predicate(block: BlockPredicatesScope.() -> Unit) {
	predicate = blockPredicate(block)
}
