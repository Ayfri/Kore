package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a player interacts with an entity.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#playerinteractedwithentity
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class PlayerInteractedWithEntity(
	override var player: EntityOrPredicates? = null,
	override var item: ItemStackPredicate? = null,
	override var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition(), EntityTrigger, ItemTrigger

/** Add a `playerInteractedWithEntity` criterion, triggered when a player interacts with an entity. */
fun AdvancementCriteria.playerInteractedWithEntity(name: String, block: PlayerInteractedWithEntity.() -> Unit = {}) {
	criteria[name] = PlayerInteractedWithEntity().apply(block)
}
