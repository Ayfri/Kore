package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Distance
import io.github.ayfri.kore.features.predicates.sub.Location
import kotlinx.serialization.Serializable

@Serializable
data class FallFromHeight(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var startPosition: Location? = null,
	var distance: Distance? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.fallFromHeight(name: String, block: FallFromHeight.() -> Unit = {}) {
	criteria += FallFromHeight(name).apply(block)
}

fun FallFromHeight.startPosition(block: Location.() -> Unit) {
	startPosition = Location().apply(block)
}

fun FallFromHeight.distance(block: Distance.() -> Unit) {
	distance = Distance().apply(block)
}
