package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Loot entry that rolls all children as a group.
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Group(
	var children: List<LootEntry>,
	var conditions: PredicateAsList? = null,
) : LootEntry()

/** Add a Group entry with optional conditions. */
fun LootEntries.group(children: List<LootEntry> = emptyList(), conditions: Predicate.() -> Unit = {}) {
	add(Group(children, Predicate().apply(conditions)))
}

/** Add and configure a Group entry. */
fun LootEntries.group(group: Group.() -> Unit = {}) {
	add(Group(emptyList()).apply(group))
}

/** Configure child entries. */
fun Group.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

/** Set conditions for this entry, see [Predicates](https://kore.ayfri.com/docs/predicates). */
fun Group.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}
