package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.loottables.entries.slotsource.SlotSource
import io.github.ayfri.kore.features.loottables.entries.slotsource.SlotSourcesBuilder
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Loot entry that selects items from inventory slots specified by a slot source.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Slots(
	var slotSource: InlinableList<SlotSource> = emptyList(),
	var conditions: PredicateAsList? = null,
	var functions: ItemModifierAsList? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add a Slots entry and configure it. */
fun LootEntries.slots(block: Slots.() -> Unit = {}) {
	add(Slots().apply(block))
}

/** Set conditions, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun Slots.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}

/** Set item modifier functions, see [ItemModifiers](https://kore.ayfri.com/docs/data-driven/item-modifiers). */
fun Slots.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}

/** Configure the slot sources for this Slots entry. */
fun Slots.slotSources(block: SlotSourcesBuilder.() -> Unit) {
	slotSource = SlotSourcesBuilder().apply(block).build()
}
