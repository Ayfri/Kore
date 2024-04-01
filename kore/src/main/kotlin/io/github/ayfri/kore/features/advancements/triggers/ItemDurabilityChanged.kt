package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class ItemDurabilityChanged(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var delta: IntRangeOrIntJson? = null,
	var durability: IntRangeOrIntJson? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.itemDurabilityChanged(name: String, block: ItemDurabilityChanged.() -> Unit = {}) {
	criteria += ItemDurabilityChanged(name).apply(block)
}

fun ItemDurabilityChanged.delta(block: IntRangeOrIntJson.() -> Unit) {
	delta = IntRangeOrIntJson().apply(block)
}

fun ItemDurabilityChanged.durability(block: IntRangeOrIntJson.() -> Unit) {
	durability = IntRangeOrIntJson().apply(block)
}

fun ItemDurabilityChanged.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
