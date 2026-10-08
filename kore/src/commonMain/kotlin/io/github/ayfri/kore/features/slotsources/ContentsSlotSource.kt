package io.github.ayfri.kore.features.slotsources

import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Selects the slots stored in the [component] of the items selected by [slotSource], empty ones included. */
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
	slotSource = buildSlotSources(block)
}
