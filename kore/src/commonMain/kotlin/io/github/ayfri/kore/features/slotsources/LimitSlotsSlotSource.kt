package io.github.ayfri.kore.features.slotsources

import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Limits the number of slots provided. */
@Serializable
@SerialName("limit_slots")
data class LimitSlotsSlotSource(
	var limit: Int = 0,
	override var slotSource: InlinableList<SlotSource> = emptyList(),
) : SlotSource(), SlotSourceHolder

/** Adds a limit slots slot source. */
fun SlotSourcesBuilder.limitSlots(limit: Int, block: LimitSlotsSlotSource.() -> Unit = {}) {
	sources += LimitSlotsSlotSource(limit).apply(block)
}
