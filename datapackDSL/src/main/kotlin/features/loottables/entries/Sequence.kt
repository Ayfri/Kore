package features.loottables.entries

import features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

@Serializable
data class Sequence(
	var children: List<LootEntry>,
	var conditions: List<PredicateCondition>? = null,
) : LootEntry

fun LootEntries.sequence(children: List<LootEntry> = emptyList(), conditions: MutableList<PredicateCondition>.() -> Unit = {}) {
	add(Sequence(children, buildList(conditions)))
}

fun LootEntries.sequence(sequence: Sequence.() -> Unit = {}) {
	add(Sequence(emptyList()).apply(sequence))
}

fun Sequence.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

fun Sequence.conditions(block: MutableList<PredicateCondition>.() -> Unit) {
	conditions = buildList(block)
}
