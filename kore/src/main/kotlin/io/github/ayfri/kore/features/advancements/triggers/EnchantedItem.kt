package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class EnchantedItem(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var item: ItemStack? = null,
	var levels: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.enchantedItem(name: String, block: EnchantedItem.() -> Unit = {}) {
	criteria += EnchantedItem(name).apply(block)
}

fun EnchantedItem.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
