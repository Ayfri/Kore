package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a villager trade occurs.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#villagertrade
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class VillagerTrade(
	override var player: EntityOrPredicates? = null,
	var item: ItemStackPredicate? = null,
	var villager: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `villagerTrade` criterion, triggered when a villager trade occurs. */
fun AdvancementCriteria.villagerTrade(name: String, block: VillagerTrade.() -> Unit = {}) {
	criteria[name] = VillagerTrade().apply(block)
}

/** Set the item constraints. */
fun VillagerTrade.item(block: ItemStackPredicate.() -> Unit) {
	item = ItemStackPredicate().apply(block)
}

/** Set the villager constraints. */
fun VillagerTrade.villager(block: EntityOrPredicates.() -> Unit) {
	villager = EntityOrPredicates().apply(block)
}
