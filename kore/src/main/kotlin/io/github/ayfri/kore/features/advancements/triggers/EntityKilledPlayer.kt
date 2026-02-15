package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DamageSource
import kotlinx.serialization.Serializable

/**
 * Triggered when an entity kills a player.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#entitykilledplayer
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class EntityKilledPlayer(
	override var player: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
	var killingBlow: DamageSource? = null,
) : AdvancementTriggerCondition()

/** Add an `entityKilledPlayer` criterion, triggered when an entity kills a player. */
fun AdvancementCriteria.entityKilledPlayer(name: String, block: EntityKilledPlayer.() -> Unit = {}) {
	criteria[name] = EntityKilledPlayer().apply(block)
}

/** Set the killer entity constraints. */
fun EntityKilledPlayer.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}

/** Set the killing blow constraints. */
fun EntityKilledPlayer.killingBlow(block: DamageSource.() -> Unit) {
	killingBlow = DamageSource().apply(block)
}
