package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class MatchTool(
	var predicate: ItemStack,
) : PredicateCondition()

fun Predicate.matchTool(vararg items: ItemArgument) {
	predicateConditions += MatchTool(ItemStack(items = items.toList()))
}

fun Predicate.matchTool(predicate: ItemStack) {
	predicateConditions += MatchTool(predicate)
}

fun Predicate.matchTool(block: ItemStack.() -> Unit = {}) {
	predicateConditions += MatchTool(ItemStack().apply(block))
}
