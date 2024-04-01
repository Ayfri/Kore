package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.LocationOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class ItemUsedOnBlock(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var location: LocationOrPredicates = LocationOrPredicates(),
) : AdvancementTriggerCondition()

fun AdvancementCriteria.itemUsedOnBlock(name: String, block: ItemUsedOnBlock.() -> Unit = {}) {
	criteria += ItemUsedOnBlock(name).apply(block)
}

fun ItemUsedOnBlock.location(block: LocationOrPredicates.() -> Unit) {
	location = LocationOrPredicates().apply(block)
}
