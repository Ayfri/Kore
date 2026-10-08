package io.github.ayfri.kore.features.slotsources

import io.github.ayfri.kore.arguments.ItemSlot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Selects the [slots] of an inventory, the context's container when [source] is `null`. */
@Serializable
@SerialName("slot_range")
data class SlotRangeSlotSource(
	var source: SlotSourceOrigin? = null,
	var slots: String,
) : SlotSource()

/** Adds a slot range slot source selecting [slot] (`ARMOR.HEAD`, `HOTBAR`), from the context's container when [source] is `null`. */
fun SlotSourcesBuilder.slotRange(source: SlotSourceOrigin?, slot: ItemSlot) = slotRange(source, slot.asString())

/** Adds a slot range slot source with a raw slot string, from the context's container when [source] is `null`. */
fun SlotSourcesBuilder.slotRange(source: SlotSourceOrigin?, slots: String) {
	sources += SlotRangeSlotSource(source, slots)
}

/** Adds a slot range slot source selecting [slot] (`ARMOR.HEAD`, `HOTBAR`) of the context's container. */
fun SlotSourcesBuilder.slotRange(slot: ItemSlot) = slotRange(null, slot)

/** Adds a slot range slot source with a raw slot string, from the context's container. */
fun SlotSourcesBuilder.slotRange(slots: String) = slotRange(null, slots)
