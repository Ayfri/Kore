package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.DistancePredicate
import io.github.ayfri.kore.features.predicates.sub.EntityPredicate
import io.github.ayfri.kore.features.predicates.sub.LocationPredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a player falls after an explosion.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#fallafterexplosion
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class FallAfterExplosion(
	override var player: EntityOrPredicates? = null,
	var startPosition: LocationPredicate = LocationPredicate(),
	var distance: DistancePredicate = DistancePredicate(),
	var cause: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `fallAfterExplosion` criterion, triggered when a player falls after an explosion. */
fun AdvancementCriteria.fallAfterExplosion(name: String, block: FallAfterExplosion.() -> Unit = {}) {
	criteria[name] = FallAfterExplosion().apply(block)
}

/** Set the start position constraints. */
fun FallAfterExplosion.startPosition(block: LocationPredicate.() -> Unit) {
	startPosition = LocationPredicate().apply(block)
}

/** Set the distance constraints. */
fun FallAfterExplosion.distance(block: DistancePredicate.() -> Unit) {
	distance = DistancePredicate().apply(block)
}

/** Set the cause constraints. */
fun FallAfterExplosion.cause(block: EntityOrPredicates.() -> Unit) {
	cause = EntityOrPredicates().apply(block)
}

/** Set the cause constraints, deprecated, prefer using Predicates instead. */
fun FallAfterExplosion.cause(entity: EntityPredicate) {
	cause = EntityOrPredicates(legacyEntity = entity)
}

/** Set the cause constraints, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun FallAfterExplosion.cause(predicate: Predicate) {
	cause = EntityOrPredicates(predicateConditions = predicate)
}
