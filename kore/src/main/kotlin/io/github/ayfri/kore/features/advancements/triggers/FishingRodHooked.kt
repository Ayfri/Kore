package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class FishingRodHooked(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
	var item: ItemStack? = null,
	var rod: ItemStack? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.fishingRodHooked(name: String, block: FishingRodHooked.() -> Unit = {}) {
	criteria += FishingRodHooked(name).apply(block)
}

fun FishingRodHooked.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}

fun FishingRodHooked.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}

fun FishingRodHooked.rod(block: ItemStack.() -> Unit) {
	rod = ItemStack().apply(block)
}
