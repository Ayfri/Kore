package features.loottables.entries

import arguments.Argument
import features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

@Serializable
data class Item(
	var name: Argument.Item,
	var conditions: List<PredicateCondition>? = null,
	var functions: List<String>? = null,
	var weight: Int? = null,
	var quality: Int? = null,
) : LootEntry

fun LootEntries.item(name: Argument.Item, block: Item.() -> Unit = {}) {
	add(Item(name).apply(block))
}

fun Item.conditions(block: MutableList<PredicateCondition>.() -> Unit) {
	conditions = buildList(block)
}

fun Item.functions(block: MutableList<String>.() -> Unit) {
	functions = buildList(block)
}
