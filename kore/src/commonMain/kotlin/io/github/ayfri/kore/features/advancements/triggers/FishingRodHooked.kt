package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a fishing rod is hooked.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#fishingrodhooked
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class FishingRodHooked(
	override var player: EntityOrPredicates? = null,
	override var entity: EntityOrPredicates? = null,
	override var item: ItemStackPredicate? = null,
	var rod: ItemStackPredicate? = null,
) : AdvancementTriggerCondition(), EntityTrigger, ItemTrigger

/** Add a `fishingRodHooked` criterion, triggered when a fishing rod is hooked. */
fun AdvancementCriteria.fishingRodHooked(name: String, block: FishingRodHooked.() -> Unit = {}) {
	criteria[name] = FishingRodHooked().apply(block)
}

/** Set the rod constraints. */
fun FishingRodHooked.rod(block: ItemStackPredicate.() -> Unit) {
	rod = ItemStackPredicate().apply(block)
}
