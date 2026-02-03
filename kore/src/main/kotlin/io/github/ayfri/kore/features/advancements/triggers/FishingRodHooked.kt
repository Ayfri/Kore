package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when a fishing rod is hooked.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#fishingrodhooked
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class FishingRodHooked(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var entity: EntityOrPredicates? = null,
	var item: ItemStack? = null,
	var rod: ItemStack? = null,
) : AdvancementTriggerCondition()

/** Add a `fishingRodHooked` criterion, triggered when a fishing rod is hooked. */
fun AdvancementCriteria.fishingRodHooked(name: String, block: FishingRodHooked.() -> Unit = {}) {
	criteria += FishingRodHooked(name).apply(block)
}

/** Set the entity constraints. */
fun FishingRodHooked.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}

/** Set the item constraints. */
fun FishingRodHooked.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}

/** Set the rod constraints. */
fun FishingRodHooked.rod(block: ItemStack.() -> Unit) {
	rod = ItemStack().apply(block)
}
