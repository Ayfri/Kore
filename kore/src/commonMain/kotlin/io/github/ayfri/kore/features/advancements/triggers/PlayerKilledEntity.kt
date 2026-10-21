package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DamageSourcePredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a player kills an entity.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#playerkilledentity
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class PlayerKilledEntity(
	override var player: EntityOrPredicates? = null,
	override var entity: EntityOrPredicates? = null,
	override var killingBlow: DamageSourcePredicate? = null,
) : AdvancementTriggerCondition(), EntityTrigger, KillingBlowTrigger

/** Add a `playerKilledEntity` criterion, triggered when a player kills an entity. */
fun AdvancementCriteria.playerKilledEntity(name: String, block: PlayerKilledEntity.() -> Unit = {}) {
	criteria[name] = PlayerKilledEntity().apply(block)
}
