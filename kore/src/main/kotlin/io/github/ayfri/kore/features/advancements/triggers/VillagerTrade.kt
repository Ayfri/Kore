package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class VillagerTrade(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var item: ItemStack? = null,
	var zombie: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.villagerTrade(name: String, block: VillagerTrade.() -> Unit = {}) {
	criteria += VillagerTrade(name).apply(block)
}

fun VillagerTrade.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}

fun VillagerTrade.zombie(block: EntityOrPredicates.() -> Unit) {
	zombie = EntityOrPredicates().apply(block)
}
