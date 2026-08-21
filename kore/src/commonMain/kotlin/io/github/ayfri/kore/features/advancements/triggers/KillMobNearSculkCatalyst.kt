package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DamageSourcePredicate
import io.github.ayfri.kore.features.predicates.sub.EntityPredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a mob is killed near a sculk catalyst.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#killmobnearsculkcatalyst
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class KillMobNearSculkCatalyst(
	override var player: EntityOrPredicates? = null,
	var entity: EntityPredicate? = null,
	var killingBlow: DamageSourcePredicate? = null,
) : AdvancementTriggerCondition()

/** Add a `killMobNearSculkCatalyst` criterion. */
fun AdvancementCriteria.killMobNearSculkCatalyst(name: String, block: KillMobNearSculkCatalyst.() -> Unit = {}) {
	criteria[name] = KillMobNearSculkCatalyst().apply(block)
}

/** Set the killed entity constraints. */
fun KillMobNearSculkCatalyst.entity(block: EntityPredicate.() -> Unit) {
	entity = EntityPredicate().apply(block)
}

/** Set the killing blow constraints. */
fun KillMobNearSculkCatalyst.killingBlow(block: DamageSourcePredicate.() -> Unit) {
	killingBlow = DamageSourcePredicate().apply(block)
}
