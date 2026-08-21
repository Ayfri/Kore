package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DistancePredicate
import io.github.ayfri.kore.features.predicates.sub.LocationPredicate
import kotlinx.serialization.Serializable

/**
 * Triggered when a player falls from a height.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#fallfromheight
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class FallFromHeight(
	override var player: EntityOrPredicates? = null,
	var startPosition: LocationPredicate? = null,
	var distance: DistancePredicate? = null,
) : AdvancementTriggerCondition()

/** Add a `fallFromHeight` criterion, triggered when a player falls from a height. */
fun AdvancementCriteria.fallFromHeight(name: String, block: FallFromHeight.() -> Unit = {}) {
	criteria[name] = FallFromHeight().apply(block)
}

/** Set the start position constraints. */
fun FallFromHeight.startPosition(block: LocationPredicate.() -> Unit) {
	startPosition = LocationPredicate().apply(block)
}

/** Set the distance constraints. */
fun FallFromHeight.distance(block: DistancePredicate.() -> Unit) {
	distance = DistancePredicate().apply(block)
}
