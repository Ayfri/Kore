package features.loottables.entries

import arguments.Argument
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierAsList
import features.predicates.Predicate
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class Item(
	var name: Argument.Item,
	var conditions: PredicateAsList? = null,
	var functions: ItemModifierAsList? = null,
	var weight: Int? = null,
	var quality: Int? = null,
) : LootEntry

fun LootEntries.item(name: Argument.Item, block: Item.() -> Unit = {}) {
	add(Item(name).apply(block))
}

fun Item.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}

fun Item.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}
