package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import io.github.ayfri.kore.features.predicates.sub.Slots
import kotlinx.serialization.Serializable

@Serializable
data class InventoryChanged(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var items: List<ItemStack>? = null,
	var slots: Slots? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.inventoryChanged(name: String, block: InventoryChanged.() -> Unit = {}) {
	criteria += InventoryChanged(name).apply(block)
}

fun InventoryChanged.item(block: ItemStack.() -> Unit) {
	items = (items ?: emptyList()) + listOf(ItemStack().apply(block))
}

fun InventoryChanged.items(block: MutableList<ItemStack>.() -> Unit) {
	items = buildList(block)
}

fun InventoryChanged.slots(block: Slots.() -> Unit) {
	slots = Slots().apply(block)
}
