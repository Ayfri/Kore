package features.worldgen.placedfeature.modifiers

import features.worldgen.blockpredicate.BlockPredicate
import features.worldgen.blockpredicate.True
import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class BlockPredicateFilter(
	var predicate: BlockPredicate = True,
) : PlacementModifier()

fun PlacedFeature.blockPredicateFilter(blockPredicate: BlockPredicate = True) {
	placementModifiers += BlockPredicateFilter(blockPredicate)
}
