package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class TameAnimal(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.tameAnimal(name: String, block: TameAnimal.() -> Unit = {}) {
	criteria += TameAnimal(name).apply(block)
}

fun TameAnimal.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}
