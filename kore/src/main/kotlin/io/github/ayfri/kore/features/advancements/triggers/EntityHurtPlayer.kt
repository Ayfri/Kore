package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Damage
import kotlinx.serialization.Serializable

@Serializable
data class EntityHurtPlayer(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var damage: Damage? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.entityHurtPlayer(name: String, block: EntityHurtPlayer.() -> Unit = {}) {
	criteria += EntityHurtPlayer(name).apply(block)
}

fun EntityHurtPlayer.damage(block: Damage.() -> Unit) {
	damage = Damage().apply(block)
}
