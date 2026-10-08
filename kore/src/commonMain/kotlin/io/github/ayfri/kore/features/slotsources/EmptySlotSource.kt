package io.github.ayfri.kore.features.slotsources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Empty selection containing no slots. */
@Serializable
@SerialName("empty")
data object EmptySlotSource : SlotSource()

/** Adds an empty slot source. */
fun SlotSourcesBuilder.empty() {
	sources += EmptySlotSource
}
