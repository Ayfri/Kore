package features.loottables.entries

import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.PredicateConditions
import kotlinx.serialization.Serializable

@Serializable
data class Group(
	var children: List<LootEntry>,
	var conditions: List<PredicateCondition>? = null,
) : LootEntry

fun LootEntries.group(children: List<LootEntry> = emptyList(), conditions: PredicateConditions.() -> Unit = {}) {
	add(Group(children, buildList(conditions)))
}

fun LootEntries.group(group: Group.() -> Unit = {}) {
	add(Group(emptyList()).apply(group))
}

fun Group.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

fun Group.conditions(block: PredicateConditions.() -> Unit) {
	conditions = buildList(block)
}
