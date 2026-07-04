package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player avoids vibrations.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#avoidvibrations
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class AvoidVibration(
	override var player: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add an `avoidVibration` criterion, triggered when a player avoids vibrations. */
fun AdvancementCriteria.avoidVibration(name: String, block: AvoidVibration.() -> Unit = {}) {
	criteria[name] = AvoidVibration().apply(block)
}
