package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.EntityPredicate
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a player shears equipment off of an EntityPredicate such as wolf armor.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#playershearedequipment
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class PlayerShearedEquipment(
	override var player: EntityOrPredicates? = null,
	/** The entity that was sheared. */
	var entity: EntityPredicate? = null,
	/** The sheared item. */
	override var item: ItemStackPredicate? = null,
) : AdvancementTriggerCondition(), ItemTrigger

/** Add a [`playerShearedEquipment`][PlayerShearedEquipment] criterion */
fun AdvancementCriteria.playerShearedEquipment(name: String, block: PlayerShearedEquipment.() -> Unit = {}) {
	criteria[name] = PlayerShearedEquipment().apply(block)
}

/** Set the sheared entity whose quipment was sheared constraints. */
fun PlayerShearedEquipment.entity(block: EntityPredicate.() -> Unit) {
	entity = EntityPredicate().apply(block)
}
