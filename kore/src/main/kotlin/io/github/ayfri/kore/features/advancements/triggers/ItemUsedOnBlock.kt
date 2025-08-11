package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.LocationOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when an item is used on a block.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#itemusedonblock
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class ItemUsedOnBlock(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var location: LocationOrPredicates = LocationOrPredicates(),
) : AdvancementTriggerCondition()

/** Add an `itemUsedOnBlock` criterion, triggered when an item is used on a block. */
fun AdvancementCriteria.itemUsedOnBlock(name: String, block: ItemUsedOnBlock.() -> Unit = {}) {
	criteria += ItemUsedOnBlock(name).apply(block)
}

/** Set the location constraints. */
fun ItemUsedOnBlock.location(block: LocationOrPredicates.() -> Unit) {
	location = LocationOrPredicates().apply(block)
}
