package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Loot entry that selects the first successful child entry (alternative of children).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Alternative(
	var children: List<LootEntry>,
	var conditions: PredicateAsList? = null,
) : LootEntry()

/** Add an Alternative entry with optional conditions. */
fun LootEntries.alternative(children: List<LootEntry> = emptyList(), conditions: Predicate.() -> Unit = {}) {
	add(Alternative(children, Predicate().apply(conditions)))
}

/** Add and configure an Alternative entry. */
fun LootEntries.alternate(alternative: Alternative.() -> Unit = {}) {
	add(Alternative(emptyList()).apply(alternative))
}

/** Configure child entries. */
fun Alternative.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

/** Set conditions for this entry, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Alternative.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}
