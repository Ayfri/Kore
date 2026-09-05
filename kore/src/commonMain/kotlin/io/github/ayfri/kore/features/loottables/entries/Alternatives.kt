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
data class Alternatives(
	var children: List<LootEntry>,
	var conditions: PredicateAsList? = null,
) : LootEntry()

/** Add an Alternatives entry with optional conditions. */
fun LootEntries.alternatives(children: List<LootEntry> = emptyList(), conditions: Alternatives.() -> Unit = {}) {
	add(Alternatives(children).apply(conditions))
}
/** Configure child entries. */
fun Alternatives.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

/** Set conditions for this entry, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Alternatives.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}
