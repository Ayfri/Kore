package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DamageSource
import kotlinx.serialization.Serializable

@Serializable
data class PlayerKilledEntity(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
	var killingBlow: DamageSource? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.playerKilledEntity(name: String, block: PlayerKilledEntity.() -> Unit = {}) {
	criteria += PlayerKilledEntity(name).apply(block)
}

fun PlayerKilledEntity.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}

fun PlayerKilledEntity.killingBlow(block: DamageSource.() -> Unit) {
	killingBlow = DamageSource().apply(block)
}
