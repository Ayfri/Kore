package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DamageSource
import kotlinx.serialization.Serializable

@Serializable
data class EntityKilledPlayer(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
	var killingBlow: DamageSource? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.entityKilledPlayer(name: String, block: EntityKilledPlayer.() -> Unit = {}) {
	criteria += EntityKilledPlayer(name).apply(block)
}

fun EntityKilledPlayer.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}

fun EntityKilledPlayer.killingBlow(block: DamageSource.() -> Unit) {
	killingBlow = DamageSource().apply(block)
}
