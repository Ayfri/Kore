package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when an item's durability changes.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#itemdurabilitychanged
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class ItemDurabilityChanged(
	override var player: EntityOrPredicates? = null,
	var delta: IntRangeOrIntJson? = null,
	var durability: IntRangeOrIntJson? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

/** Add an `itemDurabilityChanged` criterion, triggered when an item's durability changes. */
fun AdvancementCriteria.itemDurabilityChanged(name: String, block: ItemDurabilityChanged.() -> Unit = {}) {
	criteria[name] = ItemDurabilityChanged().apply(block)
}

/** Set the durability delta constraints. */
fun ItemDurabilityChanged.delta(block: IntRangeOrIntJson.() -> Unit) {
	delta = IntRangeOrIntJson().apply(block)
}

/** Set the durability constraints. */
fun ItemDurabilityChanged.durability(block: IntRangeOrIntJson.() -> Unit) {
	durability = IntRangeOrIntJson().apply(block)
}

/** Set the item constraints. */
fun ItemDurabilityChanged.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
