package io.github.ayfri.kore.features.loottables.entries.slotsource

import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Applies a filter to the selected slots, excluding any non-matching slots. */
@Serializable
@SerialName("filtered")
data class FilteredSlotSource(
	var itemFilter: ItemStackPredicate = ItemStackPredicate(),
	var slotSource: InlinableList<SlotSource> = emptyList(),
) : SlotSource()

/** Adds a filtered slot source. */
fun SlotSourcesBuilder.filtered(block: FilteredSlotSource.() -> Unit = {}) {
	sources += FilteredSlotSource().apply(block)
}

/** Configure the item filter predicate for this [FilteredSlotSource]. */
fun FilteredSlotSource.itemFilter(block: ItemStackPredicate.() -> Unit) {
	itemFilter = ItemStackPredicate().apply(block)
}

/** Configure the slot sources for this [FilteredSlotSource]. */
fun FilteredSlotSource.slotSource(block: SlotSourcesBuilder.() -> Unit) {
	slotSource = SlotSourcesBuilder().apply(block).build()
}
