package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.LocationOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.Location
import kotlinx.serialization.Serializable

/**
 * Triggered when an allay drops an item on a block.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#allaydropitemonblock
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class AllayDropItemOnBlock(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var location: LocationOrPredicates = LocationOrPredicates(),
) : AdvancementTriggerCondition()

/** Add an `allayDropItemOnBlock` criterion, triggered when an allay drops an item on a block. */
fun AdvancementCriteria.allayDropItemOnBlock(name: String, location: Location, block: AllayDropItemOnBlock.() -> Unit) {
	criteria += AllayDropItemOnBlock(name, location = LocationOrPredicates(location)).apply(block)
}

/** Add an `allayDropItemOnBlock` criterion, triggered when an allay drops an item on a block. */
fun AdvancementCriteria.allayDropItemOnBlock(name: String, predicate: Predicate, block: AllayDropItemOnBlock.() -> Unit) {
	criteria += AllayDropItemOnBlock(
		name,
		location = LocationOrPredicates(predicateConditions = predicate.predicateConditions)
	).apply(block)
}

/** Add an `allayDropItemOnBlock` criterion, triggered when an allay drops an item on a block. */
fun AdvancementCriteria.allayDropItemOnBlock(name: String, block: AllayDropItemOnBlock.() -> Unit = {}) {
	criteria += AllayDropItemOnBlock(name).apply(block)
}

/** Set the location condition, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun AllayDropItemOnBlock.location(block: Location.() -> Unit) {
	location = LocationOrPredicates(Location().apply(block))
}

/** Set the predicate conditions, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun AllayDropItemOnBlock.predicate(predicate: Predicate.() -> Unit) {
	location = LocationOrPredicates(predicateConditions = Predicate().apply(predicate).predicateConditions)
}
