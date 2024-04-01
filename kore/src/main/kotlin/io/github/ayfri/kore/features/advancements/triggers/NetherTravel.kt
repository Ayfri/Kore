package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Distance
import io.github.ayfri.kore.features.predicates.sub.Location
import kotlinx.serialization.Serializable

@Serializable
data class NetherTravel(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var distance: Distance? = null,
	var startPosition: Location? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.netherTravel(name: String, block: NetherTravel.() -> Unit = {}) {
	criteria += NetherTravel(name).apply(block)
}

fun NetherTravel.distance(block: Distance.() -> Unit) {
	distance = Distance().apply(block)
}

fun NetherTravel.startPosition(block: Location.() -> Unit) {
	startPosition = Location().apply(block)
}
