package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import io.github.ayfri.kore.features.predicates.sub.Slots
import kotlinx.serialization.Serializable

/**
 * Triggered when a player's inventory contents change.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#inventorychanged
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class InventoryChanged(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var items: List<ItemStack>? = null,
	var slots: Slots? = null,
) : AdvancementTriggerCondition()

/** Add an `inventoryChanged` criterion, triggered when a player's inventory changes. */
fun AdvancementCriteria.inventoryChanged(name: String, block: InventoryChanged.() -> Unit = {}) {
	criteria += InventoryChanged(name).apply(block)
}

/** Add one item constraint. */
fun InventoryChanged.item(block: ItemStack.() -> Unit) {
	items = (items ?: emptyList()) + listOf(ItemStack().apply(block))
}

/** Replace the item constraints list. */
fun InventoryChanged.items(block: MutableList<ItemStack>.() -> Unit) {
	items = buildList(block)
}

/** Set the slot constraints. */
fun InventoryChanged.slots(block: Slots.() -> Unit) {
	slots = Slots().apply(block)
}
