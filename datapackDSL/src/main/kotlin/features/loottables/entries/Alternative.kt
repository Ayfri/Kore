package features.loottables.entries

import features.predicates.Predicate
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class Alternative(
	var children: List<LootEntry>,
	var conditions: PredicateAsList? = null,
) : LootEntry

fun LootEntries.alternative(children: List<LootEntry> = emptyList(), conditions: Predicate.() -> Unit = {}) {
	add(Alternative(children, Predicate().apply(conditions)))
}

fun LootEntries.alternate(alternative: Alternative.() -> Unit = {}) {
	add(Alternative(emptyList()).apply(alternative))
}

fun Alternative.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

fun Alternative.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}
