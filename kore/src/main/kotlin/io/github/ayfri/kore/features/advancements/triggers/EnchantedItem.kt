package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when an item is enchanted.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#enchanteditem
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class EnchantedItem(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var item: ItemStack? = null,
	var levels: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition()

/** Add an `enchantedItem` criterion, triggered when an item is enchanted. */
fun AdvancementCriteria.enchantedItem(name: String, block: EnchantedItem.() -> Unit = {}) {
	criteria += EnchantedItem(name).apply(block)
}

/** Set the item constraints. */
fun EnchantedItem.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
