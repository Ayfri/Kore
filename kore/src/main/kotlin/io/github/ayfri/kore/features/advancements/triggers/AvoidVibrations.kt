package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player avoids vibrations.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#avoidvibrations
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class AvoidVibrations(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add an `avoidVibrations` criterion, triggered when a player avoids vibrations. */
fun AdvancementCriteria.avoidVibrations(name: String, block: AvoidVibrations.() -> Unit = {}) {
	criteria += AvoidVibrations(name).apply(block)
}
