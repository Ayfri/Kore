package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered while an item is being used.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#usingitem
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class UsingItem(
	override var player: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

/** Add a `usingItem` criterion, triggered while an item is being used. */
fun AdvancementCriteria.usingItem(name: String, block: UsingItem.() -> Unit = {}) {
	criteria[name] = UsingItem().apply(block)
}

/** Set the item constraints. */
fun UsingItem.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
