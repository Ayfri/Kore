package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when a player shears equipment off of an Entity such as wolf armor.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#playershearedequipment
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class PlayerShearedEquipment(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	/** The entity that was sheared. */
	var entity: Entity? = null,
	/** The sheared item. */
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

/** Add a [`playerShearedEquipment`][PlayerShearedEquipment] criterion */
fun AdvancementCriteria.playerShearedEquipment(name: String, block: PlayerShearedEquipment.() -> Unit = {}) {
	criteria += PlayerShearedEquipment(name).apply(block)
}

/** Set the sheared entity whose quipment was sheared constraints. */
fun PlayerShearedEquipment.entity(block: Entity.() -> Unit) {
	entity = Entity().apply(block)
}

/** Set the sheared off equipment item constraints. */
fun PlayerShearedEquipment.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
