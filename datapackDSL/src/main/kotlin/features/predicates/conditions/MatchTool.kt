package features.predicates.conditions

import features.advancements.types.ItemStack
import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class MatchTool(
	var predicate: ItemStack,
) : PredicateCondition()

fun Predicate.matchTool(predicate: ItemStack) {
	predicateConditions += MatchTool(predicate)
}

fun Predicate.matchTool(block: ItemStack.() -> Unit = {}) {
	predicateConditions += MatchTool(ItemStack().apply(block))
}
