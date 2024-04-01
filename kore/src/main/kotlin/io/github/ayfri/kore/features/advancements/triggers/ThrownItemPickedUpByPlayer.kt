package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class ThrownItemPickedUpByPlayer(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.thrownItemPickedUpByPlayer(name: String, block: ThrownItemPickedUpByPlayer.() -> Unit = {}) {
	criteria += ThrownItemPickedUpByPlayer(name).apply(block)
}

fun ThrownItemPickedUpByPlayer.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}

fun ThrownItemPickedUpByPlayer.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
