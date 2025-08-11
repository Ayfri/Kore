package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Distance
import io.github.ayfri.kore.features.predicates.sub.Location
import kotlinx.serialization.Serializable

/**
 * Triggered when a player falls from a height.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#fallfromheight
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class FallFromHeight(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var startPosition: Location? = null,
	var distance: Distance? = null,
) : AdvancementTriggerCondition()

/** Add a `fallFromHeight` criterion, triggered when a player falls from a height. */
fun AdvancementCriteria.fallFromHeight(name: String, block: FallFromHeight.() -> Unit = {}) {
	criteria += FallFromHeight(name).apply(block)
}

/** Set the start position constraints. */
fun FallFromHeight.startPosition(block: Location.() -> Unit) {
	startPosition = Location().apply(block)
}

/** Set the distance constraints. */
fun FallFromHeight.distance(block: Distance.() -> Unit) {
	distance = Distance().apply(block)
}
