package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.LocationOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player uses any block.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#anyblockuse
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class AnyBlockUse(
	override var player: EntityOrPredicates? = null,
	var location: LocationOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add an `anyBlockUse` criterion, triggered when a player uses any block. */
fun AdvancementCriteria.anyBlockUse(name: String, block: AnyBlockUse.() -> Unit = {}) {
	criteria[name] = AnyBlockUse().apply(block)
}

/** Set the location constraints, checked against the used block. */
fun AnyBlockUse.location(block: LocationOrPredicates.() -> Unit) {
	location = LocationOrPredicates().apply(block)
}
