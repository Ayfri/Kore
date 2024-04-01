package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class ShotCrossbow(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.shotCrossbow(name: String, block: ShotCrossbow.() -> Unit = {}) {
	criteria += ShotCrossbow(name).apply(block)
}

fun ShotCrossbow.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
