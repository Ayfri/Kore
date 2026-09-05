package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Loot entry that rolls all children in sequence order.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Sequence(
	var children: List<LootEntry>,
	var conditions: PredicateAsList? = null,
) : LootEntry()

/** Add a Sequence entry with optional conditions. */
fun LootEntries.sequence(children: List<LootEntry> = emptyList(), conditions: Predicate.() -> Unit = {}) {
	add(Sequence(children, Predicate().apply(conditions)))
}

/** Add and configure a Sequence entry. */
fun LootEntries.sequence(sequence: Sequence.() -> Unit = {}) {
	add(Sequence(emptyList()).apply(sequence))
}

/** Configure child entries. */
fun Sequence.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

/** Set conditions, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Sequence.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}
