package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Distance
import io.github.ayfri.kore.features.predicates.sub.Location
import kotlinx.serialization.Serializable

/**
 * Triggered when riding an entity in lava.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#rideentityinlava
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class RideEntityInLava(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var distance: Distance? = null,
	var startPosition: Location? = null,
) : AdvancementTriggerCondition()

/** Add a `rideEntityInLava` criterion, triggered when riding an entity in lava. */
fun AdvancementCriteria.rideEntityInLava(name: String, block: RideEntityInLava.() -> Unit = {}) {
	criteria += RideEntityInLava(name).apply(block)
}

/** Set the distance constraints. */
fun RideEntityInLava.distance(block: Distance.() -> Unit) {
	distance = Distance().apply(block)
}

/** Set the start position constraints. */
fun RideEntityInLava.startPosition(block: Location.() -> Unit) {
	startPosition = Location().apply(block)
}
