package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class UsedTotem(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.usedTotem(name: String, block: UsedTotem.() -> Unit = {}) {
	criteria += UsedTotem(name).apply(block)
}

fun UsedTotem.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
