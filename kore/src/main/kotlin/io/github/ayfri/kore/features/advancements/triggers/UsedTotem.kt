package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when a totem of undying is used.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#usedtotem
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class UsedTotem(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

/** Add a `usedTotem` criterion, triggered when a totem of undying is used. */
fun AdvancementCriteria.usedTotem(name: String, block: UsedTotem.() -> Unit = {}) {
	criteria += UsedTotem(name).apply(block)
}

/** Set the item constraints. */
fun UsedTotem.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
