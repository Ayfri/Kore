package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Distance
import io.github.ayfri.kore.features.predicates.sub.Location
import kotlinx.serialization.Serializable

/**
 * Triggered when a player travels through the Nether.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#nethertravel
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class NetherTravel(
	override var player: EntityOrPredicates? = null,
	var distance: Distance? = null,
	var startPosition: Location? = null,
) : AdvancementTriggerCondition()

/** Add a `netherTravel` criterion, triggered when a player travels through the Nether. */
fun AdvancementCriteria.netherTravel(name: String, block: NetherTravel.() -> Unit = {}) {
	criteria[name] = NetherTravel().apply(block)
}

/** Set the distance constraints. */
fun NetherTravel.distance(block: Distance.() -> Unit) {
	distance = Distance().apply(block)
}

/** Set the start position constraints. */
fun NetherTravel.startPosition(block: Location.() -> Unit) {
	startPosition = Location().apply(block)
}
