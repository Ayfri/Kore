package features.loottables.entries

import arguments.Argument
import features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

@Serializable
data class LootTable(
	var name: Argument.LootTable,
	var conditions: List<PredicateCondition>? = null,
	var functions: List<String>? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry

fun LootEntries.lootTable(name: Argument.LootTable, block: LootTable.() -> Unit = {}) {
	add(LootTable(name).apply(block))
}

fun LootTable.conditions(block: MutableList<PredicateCondition>.() -> Unit) {
	conditions = buildList(block)
}

fun LootTable.functions(block: MutableList<String>.() -> Unit) {
	functions = buildList(block)
}