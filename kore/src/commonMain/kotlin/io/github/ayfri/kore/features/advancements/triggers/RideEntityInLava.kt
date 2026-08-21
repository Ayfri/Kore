package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DistancePredicate
import io.github.ayfri.kore.features.predicates.sub.LocationPredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when riding an entity in lava.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#rideentityinlava
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class RideEntityInLava(
	override var player: EntityOrPredicates? = null,
	var distance: DistancePredicate? = null,
	var startPosition: LocationPredicate? = null,
) : AdvancementTriggerCondition()

/** Add a `rideEntityInLava` criterion, triggered when riding an entity in lava. */
fun AdvancementCriteria.rideEntityInLava(name: String, block: RideEntityInLava.() -> Unit = {}) {
	criteria[name] = RideEntityInLava().apply(block)
}

/** Set the distance constraints. */
fun RideEntityInLava.distance(block: DistancePredicate.() -> Unit) {
	distance = DistancePredicate().apply(block)
}

/** Set the start position constraints. */
fun RideEntityInLava.startPosition(block: LocationPredicate.() -> Unit) {
	startPosition = LocationPredicate().apply(block)
}
