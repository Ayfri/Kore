package io.github.ayfri.kore.features.loottables.entries.slotsource

import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Limits the number of slots provided. */
@Serializable
@SerialName("limit_slots")
data class LimitSlotsSlotSource(
	var limit: Int = 0,
	var slotSource: InlinableList<SlotSource> = emptyList(),
) : SlotSource()

/** Configure the slot sources for this [LimitSlotsSlotSource]. */
fun LimitSlotsSlotSource.slotSource(block: SlotSourcesBuilder.() -> Unit) {
	slotSource = SlotSourcesBuilder().apply(block).build()
}

/** Adds a limit slots slot source. */
fun SlotSourcesBuilder.limitSlots(limit: Int, block: LimitSlotsSlotSource.() -> Unit = {}) {
	sources += LimitSlotsSlotSource(limit).apply(block)
}
