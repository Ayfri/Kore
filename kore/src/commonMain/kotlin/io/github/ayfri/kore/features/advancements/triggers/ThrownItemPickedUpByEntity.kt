package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a thrown item is picked up by an entity.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#thrownitempickedupbyentity
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class ThrownItemPickedUpByEntity(
	override var player: EntityOrPredicates? = null,
	override var entity: EntityOrPredicates? = null,
	override var item: ItemStackPredicate? = null,
) : AdvancementTriggerCondition(), EntityTrigger, ItemTrigger

/** Add a `thrownItemPickedUpByEntity` criterion, triggered when a thrown item is picked up by an entity. */
fun AdvancementCriteria.thrownItemPickedUpByEntity(name: String, block: ThrownItemPickedUpByEntity.() -> Unit = {}) {
	criteria[name] = ThrownItemPickedUpByEntity().apply(block)
}
