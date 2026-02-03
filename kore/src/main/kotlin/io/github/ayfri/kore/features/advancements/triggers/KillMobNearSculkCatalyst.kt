package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DamageSource
import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

/**
 * Triggered when a mob is killed near a sculk catalyst.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#killmobnearsculkcatalyst
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class KillMobNearSculkCatalyst(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: Entity? = null,
	var killingBlow: DamageSource? = null,
) : AdvancementTriggerCondition()

/** Add a `killMobNearSculkCatalyst` criterion. */
fun AdvancementCriteria.killMobNearSculkCatalyst(name: String, block: KillMobNearSculkCatalyst.() -> Unit = {}) {
	criteria += KillMobNearSculkCatalyst(name).apply(block)
}

/** Set the killed entity constraints. */
fun KillMobNearSculkCatalyst.entity(block: Entity.() -> Unit) {
	entity = Entity().apply(block)
}

/** Set the killing blow constraints. */
fun KillMobNearSculkCatalyst.killingBlow(block: DamageSource.() -> Unit) {
	killingBlow = DamageSource().apply(block)
}
