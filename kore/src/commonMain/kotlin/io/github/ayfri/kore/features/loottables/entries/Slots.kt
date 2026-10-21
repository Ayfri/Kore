package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.slotsources.SlotSource
import io.github.ayfri.kore.features.slotsources.SlotSourcesBuilder
import io.github.ayfri.kore.features.slotsources.buildSlotSources
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
	override var condition: PredicateCondition? = null,
	override var modifier: InlinableItemModifier? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add a Slots entry and configure it. */
fun LootEntries.slots(block: Slots.() -> Unit = {}) {
	add(Slots().apply(block))
}

/** Configure the slot sources for this Slots entry. */
fun Slots.slotSources(block: SlotSourcesBuilder.() -> Unit) {
	slotSource = buildSlotSources(block)
}
