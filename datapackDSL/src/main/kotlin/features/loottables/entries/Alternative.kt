package features.loottables.entries

import features.predicates.conditions.PredicateConditions
import kotlinx.serialization.Serializable

@Serializable
data class Alternative(
	var children: List<LootEntry>,
	var conditions: PredicateConditions? = null,
) : LootEntry

fun LootEntries.alternative(children: List<LootEntry> = emptyList(), conditions: PredicateConditions.() -> Unit = {}) {
	add(Alternative(children, buildList(conditions)))
}

fun LootEntries.alternate(alternative: Alternative.() -> Unit = {}) {
	add(Alternative(emptyList()).apply(alternative))
}

fun Alternative.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

fun Alternative.conditions(block: PredicateConditions.() -> Unit) {
	conditions = buildList(block)
}
