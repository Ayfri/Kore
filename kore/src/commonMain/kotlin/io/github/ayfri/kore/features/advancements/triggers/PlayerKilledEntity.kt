package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DamageSource
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
	var entity: EntityOrPredicates? = null,
	var killingBlow: DamageSource? = null,
) : AdvancementTriggerCondition()

/** Add a `playerKilledEntity` criterion, triggered when a player kills an entity. */
fun AdvancementCriteria.playerKilledEntity(name: String, block: PlayerKilledEntity.() -> Unit = {}) {
	criteria[name] = PlayerKilledEntity().apply(block)
}

/** Set the killed entity constraints. */
fun PlayerKilledEntity.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}

/** Set the killing blow constraints. */
fun PlayerKilledEntity.killingBlow(block: DamageSource.() -> Unit) {
	killingBlow = DamageSource().apply(block)
}
