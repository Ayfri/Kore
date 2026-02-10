package io.github.ayfri.kore.features.loottables.entries.slotsource

import io.github.ayfri.kore.arguments.ItemSlotWrapper
import io.github.ayfri.kore.arguments.RangeItemSlot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Selects slots within a slot range from the inventory of an entity or block entity. */
@Serializable
@SerialName("slot_range")
data class SlotRangeSlotSource(
	var source: SlotSourceOrigin,
	var slots: String,
) : SlotSource()

/** Adds a slot range slot source for a [RangeItemSlot] (e.g., `HOTBAR`, `ARMOR`). */
fun SlotSourcesBuilder.slotRange(source: SlotSourceOrigin, slot: RangeItemSlot) {
	sources += SlotRangeSlotSource(source, slot.all())
}

/** Adds a slot range slot source for a specific [ItemSlotWrapper]. */
fun SlotSourcesBuilder.slotRange(source: SlotSourceOrigin, slot: ItemSlotWrapper) {
	sources += SlotRangeSlotSource(source, slot.name())
}

/** Adds a slot range slot source with a raw slot string. */
fun SlotSourcesBuilder.slotRange(source: SlotSourceOrigin, slots: String) {
	sources += SlotRangeSlotSource(source, slots)
}
