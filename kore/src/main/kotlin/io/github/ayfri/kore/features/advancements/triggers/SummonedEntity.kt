package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class SummonedEntity(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.summonedEntity(name: String, block: SummonedEntity.() -> Unit = {}) {
	criteria += SummonedEntity(name).apply(block)
}

fun SummonedEntity.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}
