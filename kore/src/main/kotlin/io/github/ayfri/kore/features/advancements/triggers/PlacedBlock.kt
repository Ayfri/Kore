package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.LocationOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a block is placed.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#placedblock
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class PlacedBlock(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var location: LocationOrPredicates = LocationOrPredicates(),
) : AdvancementTriggerCondition()

/** Add a `placedBlock` criterion, triggered when a block is placed. */
fun AdvancementCriteria.placedBlock(name: String, block: PlacedBlock.() -> Unit = {}) {
	criteria += PlacedBlock(name).apply(block)
}

/** Set the location constraints. */
fun PlacedBlock.location(block: LocationOrPredicates.() -> Unit) {
	location = LocationOrPredicates().apply(block)
}
