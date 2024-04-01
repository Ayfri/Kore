package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class CuredZombieVillager(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var villager: EntityOrPredicates? = null,
	var zombie: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.curedZombieVillager(name: String, block: CuredZombieVillager.() -> Unit = {}) {
	criteria += CuredZombieVillager(name).apply(block)
}

fun CuredZombieVillager.villager(block: EntityOrPredicates.() -> Unit) {
	villager = EntityOrPredicates().apply(block)
}

fun CuredZombieVillager.zombie(block: EntityOrPredicates.() -> Unit) {
	zombie = EntityOrPredicates().apply(block)
}
