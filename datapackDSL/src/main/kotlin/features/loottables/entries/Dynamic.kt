package features.loottables.entries

import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierAsList
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.PredicateConditions
import kotlinx.serialization.Serializable

@Serializable
data class Dynamic(
	var name: String,
	var conditions: List<PredicateCondition>? = null,
	var functions: ItemModifierAsList? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry

fun LootEntries.dynamic(name: String, block: Dynamic.() -> Unit = {}) {
	add(Dynamic(name).apply(block))
}

fun Dynamic.conditions(block: PredicateConditions.() -> Unit) {
	conditions = buildList(block)
}

fun Dynamic.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}
