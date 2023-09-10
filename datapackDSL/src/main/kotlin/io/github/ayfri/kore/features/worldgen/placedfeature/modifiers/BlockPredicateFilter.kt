package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class BlockPredicateFilter(
	var predicate: BlockPredicate = True,
) : PlacementModifier()

fun PlacedFeature.blockPredicateFilter(blockPredicate: BlockPredicate = True) {
	placementModifiers += BlockPredicateFilter(blockPredicate)
}
