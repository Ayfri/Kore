package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
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
	var condition: PredicateCondition? = null,
	var modifier: InlinableItemModifier? = null,
) : LootEntry()

/** Add a Sequence entry with an optional condition. */
fun LootEntries.sequence(children: List<LootEntry> = emptyList(), condition: Predicate.() -> Unit = {}) {
	add(Sequence(children, Predicate().apply(condition).takeIf { it.predicateConditions.isNotEmpty() }?.toCondition()))
}

/** Add and configure a Sequence entry. */
fun LootEntries.sequence(sequence: Sequence.() -> Unit = {}) {
	add(Sequence(emptyList()).apply(sequence))
}

/** Configure child entries. */
fun Sequence.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

/** Set the condition, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Sequence.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the condition to the predicate [predicate], written as its bare ID. */
fun Sequence.condition(predicate: PredicateArgument) {
	condition = PredicateReference(predicate)
}

/** Set the item modifier, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun Sequence.modifier(block: ItemModifier.() -> Unit) {
	modifier = ItemModifier().apply(block)
}
