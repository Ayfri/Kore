package features.loottables.entries

import features.predicates.Predicate
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class Group(
	var children: List<LootEntry>,
	var conditions: PredicateAsList? = null,
) : LootEntry

fun LootEntries.group(children: List<LootEntry> = emptyList(), conditions: Predicate.() -> Unit = {}) {
	add(Group(children, Predicate().apply(conditions)))
}

fun LootEntries.group(group: Group.() -> Unit = {}) {
	add(Group(emptyList()).apply(group))
}

fun Group.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

fun Group.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}
