package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when a thrown item is picked up by a player.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#thrownitempickedupbyplayer
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class ThrownItemPickedUpByPlayer(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

/** Add a `thrownItemPickedUpByPlayer` criterion, triggered when a thrown item is picked up by a player. */
fun AdvancementCriteria.thrownItemPickedUpByPlayer(name: String, block: ThrownItemPickedUpByPlayer.() -> Unit = {}) {
	criteria += ThrownItemPickedUpByPlayer(name).apply(block)
}

/** Set the picker entity constraints. */
fun ThrownItemPickedUpByPlayer.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}

/** Set the item constraints. */
fun ThrownItemPickedUpByPlayer.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
