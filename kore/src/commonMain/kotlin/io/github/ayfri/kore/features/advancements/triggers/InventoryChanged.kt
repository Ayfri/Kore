package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a player's inventory contents change.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#inventorychanged
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class InventoryChanged(
	override var player: EntityOrPredicates? = null,
	var items: List<ItemStackPredicate>? = null,
	var slots: InventorySlotsPredicate? = null,
) : AdvancementTriggerCondition()

/** Add an `inventoryChanged` criterion, triggered when a player's inventory changes. */
fun AdvancementCriteria.inventoryChanged(name: String, block: InventoryChanged.() -> Unit = {}) {
	criteria[name] = InventoryChanged().apply(block)
}

/** Add one item constraint. */
fun InventoryChanged.item(block: ItemStackPredicate.() -> Unit) {
	items = (items ?: emptyList()) + listOf(ItemStackPredicate().apply(block))
}

/** Replace the item constraints list. */
fun InventoryChanged.items(block: MutableList<ItemStackPredicate>.() -> Unit) {
	items = buildList(block)
}

/** Set the slot constraints. */
fun InventoryChanged.slots(block: InventorySlotsPredicate.() -> Unit) {
	slots = InventorySlotsPredicate().apply(block)
}
