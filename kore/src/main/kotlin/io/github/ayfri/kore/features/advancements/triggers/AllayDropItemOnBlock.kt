package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.LocationOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.Location
import kotlinx.serialization.Serializable

@Serializable
data class AllayDropItemOnBlock(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var location: LocationOrPredicates = LocationOrPredicates(),
) : AdvancementTriggerCondition()

fun AdvancementCriteria.allayDropItemOnBlock(name: String, location: Location, block: AllayDropItemOnBlock.() -> Unit) {
	criteria += AllayDropItemOnBlock(name, location = LocationOrPredicates(location)).apply(block)
}

fun AdvancementCriteria.allayDropItemOnBlock(name: String, predicate: Predicate, block: AllayDropItemOnBlock.() -> Unit) {
	criteria += AllayDropItemOnBlock(
		name,
		location = LocationOrPredicates(predicateConditions = predicate.predicateConditions)
	).apply(block)
}

fun AdvancementCriteria.allayDropItemOnBlock(name: String, block: AllayDropItemOnBlock.() -> Unit = {}) {
	criteria += AllayDropItemOnBlock(name).apply(block)
}

fun AllayDropItemOnBlock.location(block: Location.() -> Unit) {
	location = LocationOrPredicates(Location().apply(block))
}

fun AllayDropItemOnBlock.predicate(predicate: Predicate.() -> Unit) {
	location = LocationOrPredicates(predicateConditions = Predicate().apply(predicate).predicateConditions)
}
