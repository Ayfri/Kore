package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player enters a specific location.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#location
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class Location(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `location` criterion, triggered when a player enters a specific location. */
fun AdvancementCriteria.location(name: String, block: Location.() -> Unit = {}) {
	criteria += Location(name).apply(block)
}
