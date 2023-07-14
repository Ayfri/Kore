package features.loottables.entries

import features.predicates.Predicate
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class Sequence(
	var children: List<LootEntry>,
	var conditions: PredicateAsList? = null,
) : LootEntry

fun LootEntries.sequence(children: List<LootEntry> = emptyList(), conditions: Predicate.() -> Unit = {}) {
	add(Sequence(children, Predicate().apply(conditions)))
}

fun LootEntries.sequence(sequence: Sequence.() -> Unit = {}) {
	add(Sequence(emptyList()).apply(sequence))
}

fun Sequence.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

fun Sequence.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}
