package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DamagePredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a player hurts an entity.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#playerhurtentity
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class PlayerHurtEntity(
	override var player: EntityOrPredicates? = null,
	override var damage: DamagePredicate? = null,
	override var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition(), DamageTrigger, EntityTrigger

/** Add a `playerHurtEntity` criterion, triggered when a player hurts an entity. */
fun AdvancementCriteria.playerHurtEntity(name: String, block: PlayerHurtEntity.() -> Unit = {}) {
	criteria[name] = PlayerHurtEntity().apply(block)
}
