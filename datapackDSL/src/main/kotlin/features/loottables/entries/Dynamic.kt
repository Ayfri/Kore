package features.loottables.entries

import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierAsList
import features.predicates.Predicate
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class Dynamic(
	var name: String,
	var conditions: PredicateAsList? = null,
	var functions: ItemModifierAsList? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

fun LootEntries.dynamic(name: String, block: Dynamic.() -> Unit = {}) {
	add(Dynamic(name).apply(block))
}

fun Dynamic.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}

fun Dynamic.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}
