package io.github.ayfri.kore.features.slotsources

import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/** Selects the slots stored in the [component] of the items selected by [slotSource], empty ones included. */
@Serializable
data class ContentsSlotSource(
	var component: InventoryComponentType,
	override var slotSource: InlinableList<SlotSource> = emptyList(),
) : SlotSource(), SlotSourceHolder

/** Adds a contents slot source. */
fun SlotSourcesBuilder.contents(component: InventoryComponentType, block: ContentsSlotSource.() -> Unit = {}) {
	sources += ContentsSlotSource(component).apply(block)
}
