package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
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
	var item: ItemStack? = null,
	var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `playerInteractedWithEntity` criterion, triggered when a player interacts with an entity. */
fun AdvancementCriteria.playerInteractedWithEntity(name: String, block: PlayerInteractedWithEntity.() -> Unit = {}) {
	criteria[name] = PlayerInteractedWithEntity().apply(block)
}

/** Set the item constraints. */
fun PlayerInteractedWithEntity.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}

/** Set the entity constraints. */
fun PlayerInteractedWithEntity.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}
