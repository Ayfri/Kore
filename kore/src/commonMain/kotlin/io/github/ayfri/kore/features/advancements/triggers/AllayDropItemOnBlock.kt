package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.LocationOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.LocationCheck
import io.github.ayfri.kore.features.predicates.sub.LocationPredicate
import io.github.ayfri.kore.features.predicates.toCondition
import kotlinx.serialization.Serializable

/**
 * Triggered when an allay drops an item on a block.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#allaydropitemonblock
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class AllayDropItemOnBlock(
	override var player: EntityOrPredicates? = null,
	var location: LocationOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add an `allayDropItemOnBlock` criterion, triggered when an allay drops an item on a block. */
fun AdvancementCriteria.allayDropItemOnBlock(name: String, location: LocationPredicate, block: AllayDropItemOnBlock.() -> Unit) {
	criteria[name] = AllayDropItemOnBlock(location = LocationOrPredicates(LocationCheck(predicate = location))).apply(block)
}

/** Add an `allayDropItemOnBlock` criterion, triggered when an allay drops an item on a block. */
fun AdvancementCriteria.allayDropItemOnBlock(name: String, predicate: Predicate, block: AllayDropItemOnBlock.() -> Unit) {
	criteria[name] = AllayDropItemOnBlock(location = LocationOrPredicates(predicate.toCondition())).apply(block)
}

/** Add an `allayDropItemOnBlock` criterion, triggered when an allay drops an item on a block. */
fun AdvancementCriteria.allayDropItemOnBlock(name: String, block: AllayDropItemOnBlock.() -> Unit = {}) {
	criteria[name] = AllayDropItemOnBlock().apply(block)
}

/** Set the location condition, as a `location_check` condition. */
fun AllayDropItemOnBlock.location(block: LocationPredicate.() -> Unit) {
	location = LocationOrPredicates(LocationCheck(predicate = LocationPredicate().apply(block)))
}

/** Set the predicate condition, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun AllayDropItemOnBlock.predicate(predicate: Predicate.() -> Unit) {
	location = LocationOrPredicates(Predicate().apply(predicate).toCondition())
}
