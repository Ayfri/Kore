package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.toCondition
import kotlinx.serialization.Serializable

/**
 * Loot entry that rolls all children as a group.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Group(
	override var children: List<LootEntry>,
	override var condition: PredicateCondition? = null,
	override var modifier: InlinableItemModifier? = null,
) : LootEntry(), CompositeLootEntry

/** Add a Group entry with an optional condition. */
fun LootEntries.group(children: List<LootEntry> = emptyList(), condition: Predicate.() -> Unit = {}) {
	add(Group(children, Predicate().apply(condition).takeIf { it.predicateConditions.isNotEmpty() }?.toCondition()))
}

/** Add and configure a Group entry. */
fun LootEntries.group(group: Group.() -> Unit = {}) {
	add(Group(emptyList()).apply(group))
}
