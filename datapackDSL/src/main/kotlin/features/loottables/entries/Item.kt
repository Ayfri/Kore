package features.loottables.entries

import arguments.Argument
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierAsList
import features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

@Serializable
data class Item(
	var name: Argument.Item,
	var conditions: List<PredicateCondition>? = null,
	var functions: ItemModifierAsList? = null,
	var weight: Int? = null,
	var quality: Int? = null,
) : LootEntry

fun LootEntries.item(name: Argument.Item, block: Item.() -> Unit = {}) {
	add(Item(name).apply(block))
}

fun Item.conditions(block: MutableList<PredicateCondition>.() -> Unit) {
	conditions = buildList(block)
}

fun Item.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}
