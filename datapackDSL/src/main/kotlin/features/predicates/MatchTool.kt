package features.predicates

import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class MatchTool(
	var predicate: ItemStack
) : Predicate
