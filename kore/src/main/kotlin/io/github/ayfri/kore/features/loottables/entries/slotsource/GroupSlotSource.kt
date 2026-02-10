package io.github.ayfri.kore.features.loottables.entries.slotsource

import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Merges several slot sources into one. */
@Serializable
@SerialName("group")
data class GroupSlotSource(
	var terms: InlinableList<SlotSource> = emptyList(),
) : SlotSource()

/** Adds a group slot source. */
fun SlotSourcesBuilder.group(block: SlotSourcesBuilder.() -> Unit) {
	sources += GroupSlotSource(SlotSourcesBuilder().apply(block).build())
}
