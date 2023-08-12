package features.worldgen.placedfeature.modifiers

import features.worldgen.blockpredicate.BlockPredicate
import features.worldgen.blockpredicate.True
import kotlinx.serialization.Serializable

@Serializable
data class BlockPredicateFilter(
	var predicate: BlockPredicate = True,
) : PlacementModifier()

fun blockPredicateFilter(blockPredicate: BlockPredicate = True) = BlockPredicateFilter(blockPredicate)

fun MutableList<PlacementModifier>.blockPredicateFilter(blockPredicate: BlockPredicate = True) {
	this += BlockPredicateFilter(blockPredicate)
}
