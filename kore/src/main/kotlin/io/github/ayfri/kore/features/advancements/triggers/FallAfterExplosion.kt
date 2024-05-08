package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.Distance
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.features.predicates.sub.Location
import kotlinx.serialization.Serializable

@Serializable
data class FallAfterExplosion(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var startPosition: Location = Location(),
	var distance: Distance = Distance(),
	var cause: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.fallAfterExplosion(name: String, block: FallAfterExplosion.() -> Unit = {}) {
	criteria += FallAfterExplosion(name).apply(block)
}

fun FallAfterExplosion.startPosition(block: Location.() -> Unit) {
	startPosition = Location().apply(block)
}

fun FallAfterExplosion.distance(block: Distance.() -> Unit) {
	distance = Distance().apply(block)
}

fun FallAfterExplosion.cause(block: EntityOrPredicates.() -> Unit) {
	cause = EntityOrPredicates().apply(block)
}

fun FallAfterExplosion.cause(entity: Entity) {
	cause = EntityOrPredicates(legacyEntity = entity)
}

fun FallAfterExplosion.cause(vararg predicates: Predicate) {
	cause = EntityOrPredicates(predicateConditions = predicates.map { it.predicateConditions }.flatten())
}
