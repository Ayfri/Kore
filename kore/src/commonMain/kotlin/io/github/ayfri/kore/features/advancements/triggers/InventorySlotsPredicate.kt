package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

/**
 * Matches how the slots of a player inventory are filled, as the `slots` key of the [InventoryChanged] trigger.
 *
 * Minecraft Wiki: [Advancement](https://minecraft.wiki/w/Advancement/JSON_format)
 */
@Serializable
data class InventorySlotsPredicate(
	/** Number of slots holding nothing. */
	var empty: IntRangeOrIntJson? = null,
	/** Number of slots holding a full stack. */
	var full: IntRangeOrIntJson? = null,
	/** Number of slots holding at least one item. */
	var occupied: IntRangeOrIntJson? = null,
)

/** Creates an [InventorySlotsPredicate]. */
fun inventorySlotsPredicate(
	empty: IntRangeOrIntJson? = null,
	full: IntRangeOrIntJson? = null,
	occupied: IntRangeOrIntJson? = null,
) = InventorySlotsPredicate(empty, full, occupied)
