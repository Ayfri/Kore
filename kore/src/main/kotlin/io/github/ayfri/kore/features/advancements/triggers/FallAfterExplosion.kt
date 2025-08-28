package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.Distance
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.features.predicates.sub.Location
import kotlinx.serialization.Serializable

/**
 * Triggered when a player falls after an explosion.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#fallafterexplosion
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class FallAfterExplosion(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var startPosition: Location = Location(),
	var distance: Distance = Distance(),
	var cause: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `fallAfterExplosion` criterion, triggered when a player falls after an explosion. */
fun AdvancementCriteria.fallAfterExplosion(name: String, block: FallAfterExplosion.() -> Unit = {}) {
	criteria += FallAfterExplosion(name).apply(block)
}

/** Set the start position constraints. */
fun FallAfterExplosion.startPosition(block: Location.() -> Unit) {
	startPosition = Location().apply(block)
}

/** Set the distance constraints. */
fun FallAfterExplosion.distance(block: Distance.() -> Unit) {
	distance = Distance().apply(block)
}

/** Set the cause constraints. */
fun FallAfterExplosion.cause(block: EntityOrPredicates.() -> Unit) {
	cause = EntityOrPredicates().apply(block)
}

/** Set the cause constraints, deprecated, prefer using Predicates instead. */
fun FallAfterExplosion.cause(entity: Entity) {
	cause = EntityOrPredicates(legacyEntity = entity)
}

/** Set the cause constraints, see [Predicates](https://kore.ayfri.com/docs/predicates). */
fun FallAfterExplosion.cause(predicate: Predicate) {
	cause = EntityOrPredicates(predicateConditions = predicate)
}
