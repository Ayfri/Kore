package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Damage
import kotlinx.serialization.Serializable

/**
 * Triggered when an entity hurts a player.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#entityhurtplayer
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class EntityHurtPlayer(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var damage: Damage? = null,
) : AdvancementTriggerCondition()

/** Add an `entityHurtPlayer` criterion, triggered when a player takes damage from an entity. */
fun AdvancementCriteria.entityHurtPlayer(name: String, block: EntityHurtPlayer.() -> Unit = {}) {
	criteria += EntityHurtPlayer(name).apply(block)
}

/** Set the damage constraints. */
fun EntityHurtPlayer.damage(block: Damage.() -> Unit) {
	damage = Damage().apply(block)
}
