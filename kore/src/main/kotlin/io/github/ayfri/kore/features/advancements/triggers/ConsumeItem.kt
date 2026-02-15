package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when a player consumes an item.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#consumeitem
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format#consume_item
 */
@Serializable
data class ConsumeItem(
	override var player: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.consumeItem(name: String, item: ItemStack? = null, block: ConsumeItem.() -> Unit = {}) {
	criteria[name] = ConsumeItem(item = item).apply(block)
}

fun AdvancementCriteria.consumeItem(name: String, vararg item: ItemArgument, block: ConsumeItem.() -> Unit = {}) {
	criteria[name] = ConsumeItem(item = ItemStack(items = item.toList())).apply(block)
}

fun ConsumeItem.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}

fun ConsumeItem.item(vararg items: ItemArgument) {
	item = ItemStack(items = items.toList())
}
