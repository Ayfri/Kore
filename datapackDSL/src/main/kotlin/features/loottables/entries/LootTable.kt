package features.loottables.entries

import arguments.types.resources.LootTableArgument
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierAsList
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class LootTable(
	var name: LootTableArgument,
	var conditions: PredicateAsList? = null,
	var functions: ItemModifierAsList? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry

fun LootEntries.lootTable(name: LootTableArgument, block: LootTable.() -> Unit = {}) {
	add(LootTable(name).apply(block))
}

fun LootTable.conditions(block: PredicateAsList.() -> Unit) {
	conditions = PredicateAsList().apply(block)
}

fun LootTable.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}
