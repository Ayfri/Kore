package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
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
	var item: ItemStackPredicate? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.consumeItem(name: String, item: ItemStackPredicate? = null, block: ConsumeItem.() -> Unit = {}) {
	criteria[name] = ConsumeItem(item = item).apply(block)
}

fun AdvancementCriteria.consumeItem(name: String, vararg item: ItemArgument, block: ConsumeItem.() -> Unit = {}) {
	criteria[name] = ConsumeItem(item = ItemStackPredicate(items = item.toList())).apply(block)
}

fun ConsumeItem.item(block: ItemStackPredicate.() -> Unit) {
	item = ItemStackPredicate().apply(block)
}

fun ConsumeItem.item(vararg items: ItemArgument) {
	item = ItemStackPredicate(items = items.toList())
}
