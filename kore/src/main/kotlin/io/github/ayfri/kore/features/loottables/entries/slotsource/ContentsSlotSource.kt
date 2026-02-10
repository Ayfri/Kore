package io.github.ayfri.kore.features.loottables.entries.slotsource

import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Selects all non-empty slots from the inventory component of one or more items. */
@Serializable
@SerialName("contents")
data class ContentsSlotSource(
	var component: InventoryComponentType,
	var slotSource: InlinableList<SlotSource> = emptyList(),
) : SlotSource()

/** Adds a contents slot source. */
fun SlotSourcesBuilder.contents(component: InventoryComponentType, block: ContentsSlotSource.() -> Unit = {}) {
	sources += ContentsSlotSource(component).apply(block)
}

/** Configure the slot sources for this [ContentsSlotSource]. */
fun ContentsSlotSource.slotSource(block: SlotSourcesBuilder.() -> Unit) {
	slotSource = SlotSourcesBuilder().apply(block).build()
}
