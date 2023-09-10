package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class Item(
	var name: ItemArgument,
	var conditions: PredicateAsList? = null,
	var functions: ItemModifierAsList? = null,
	var weight: Int? = null,
	var quality: Int? = null,
) : LootEntry()

fun LootEntries.item(name: ItemArgument, block: Item.() -> Unit = {}) {
	add(Item(name).apply(block))
}

fun Item.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}

fun Item.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}
