package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Damage
import kotlinx.serialization.Serializable

@Serializable
data class PlayerHurtEntity(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var damage: Damage? = null,
	var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.playerHurtEntity(name: String, block: PlayerHurtEntity.() -> Unit = {}) {
	criteria += PlayerHurtEntity(name).apply(block)
}

fun PlayerHurtEntity.damage(block: Damage.() -> Unit) {
	damage = Damage().apply(block)
}

fun PlayerHurtEntity.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}
