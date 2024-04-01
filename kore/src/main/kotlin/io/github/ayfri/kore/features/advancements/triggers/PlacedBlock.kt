package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.LocationOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class PlacedBlock(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var location: LocationOrPredicates = LocationOrPredicates(),
) : AdvancementTriggerCondition()

fun AdvancementCriteria.placedBlock(name: String, block: PlacedBlock.() -> Unit = {}) {
	criteria += PlacedBlock(name).apply(block)
}

fun PlacedBlock.location(block: LocationOrPredicates.() -> Unit) {
	location = LocationOrPredicates().apply(block)
}
