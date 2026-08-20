package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * A test on the state of the block at a given position, used by the placement modifiers of the placed features, by
 * numerous configured features and by the enchantment effects.
 *
 * Every builder is an extension on [BlockPredicateScope], so they only resolve inside a block that actually accepts a
 * block predicate, such as `blockPredicateFilter { }`, `lake("...") { }` or `rule { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
@GeneratedSealedSerializer
@Serializable(with = BlockPredicate.Companion.BlockPredicateSerializer::class)
sealed class BlockPredicate {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object BlockPredicateSerializer :
			NamespacedPolymorphicSerializer<BlockPredicate>(blockPredicateSealedSerializer())
	}
}

/**
 * A [BlockPredicate] tested at a position offset from the one being placed, instead of at the position itself.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 *
 * @property offset The `[X, Y, Z]` block offset to test at, each component between `-16` and `16`, `[0, 0, 0]` when `null`.
 */
sealed interface OffsetBlockPredicate {
	var offset: TripleAsArray<Int, Int, Int>?
}

/**
 * Tests the predicate at the block offset by [x], [y] and [z] from the position being placed.
 *
 * Each component has to be between `-16` and `16`.
 *
 * ```kotlin
 * solid { offset(0, -1, 0) }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun OffsetBlockPredicate.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}
