package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
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
	var children: List<LootEntry>,
	var condition: PredicateCondition? = null,
	var modifier: InlinableItemModifier? = null,
) : LootEntry()

/** Add a Group entry with an optional condition. */
fun LootEntries.group(children: List<LootEntry> = emptyList(), condition: Predicate.() -> Unit = {}) {
	add(Group(children, Predicate().apply(condition).takeIf { it.predicateConditions.isNotEmpty() }?.toCondition()))
}

/** Add and configure a Group entry. */
fun LootEntries.group(group: Group.() -> Unit = {}) {
	add(Group(emptyList()).apply(group))
}

/** Configure child entries. */
fun Group.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

/** Set the condition, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Group.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the item modifier, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun Group.modifier(block: ItemModifier.() -> Unit) {
	modifier = ItemModifier().apply(block)
}
