package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DamageSource
import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

@Serializable
data class KillMobNearSculkCatalyst(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: Entity? = null,
	var killingBlow: DamageSource? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.killMobNearSculkCatalyst(name: String, block: KillMobNearSculkCatalyst.() -> Unit = {}) {
	criteria += KillMobNearSculkCatalyst(name).apply(block)
}

fun KillMobNearSculkCatalyst.entity(block: Entity.() -> Unit) {
	entity = Entity().apply(block)
}

fun KillMobNearSculkCatalyst.killingBlow(block: DamageSource.() -> Unit) {
	killingBlow = DamageSource().apply(block)
}
