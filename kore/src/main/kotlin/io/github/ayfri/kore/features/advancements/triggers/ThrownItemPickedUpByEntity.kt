package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class ThrownItemPickedUpByEntity(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.thrownItemPickedUpByEntity(name: String, block: ThrownItemPickedUpByEntity.() -> Unit = {}) {
	criteria += ThrownItemPickedUpByEntity(name).apply(block)
}

fun ThrownItemPickedUpByEntity.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}

fun ThrownItemPickedUpByEntity.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
