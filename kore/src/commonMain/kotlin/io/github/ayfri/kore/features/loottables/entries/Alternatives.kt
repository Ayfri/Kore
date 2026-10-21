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
 * Loot entry that selects the first successful child entry (alternative of children).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Alternatives(
	var children: List<LootEntry>,
	var condition: PredicateCondition? = null,
	var modifier: InlinableItemModifier? = null,
) : LootEntry()

/** Add an Alternatives entry with optional condition and modifier. */
fun LootEntries.alternatives(children: List<LootEntry> = emptyList(), block: Alternatives.() -> Unit = {}) {
	add(Alternatives(children).apply(block))
}

/** Configure child entries. */
fun Alternatives.children(block: LootEntries.() -> Unit) {
	children = buildList(block)
}

/** Set the condition, several conditions being wrapped in an `all_of`, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Alternatives.condition(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the condition to the predicate [predicate], written as its bare ID. */
fun Alternatives.condition(predicate: PredicateArgument) {
	condition = PredicateReference(predicate)
}

/** Set the item modifier, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun Alternatives.modifier(block: ItemModifier.() -> Unit) {
	modifier = ItemModifier().apply(block)
}
