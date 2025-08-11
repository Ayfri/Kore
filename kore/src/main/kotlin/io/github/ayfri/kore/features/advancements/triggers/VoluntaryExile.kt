package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player causes a raid in a village (voluntary exile).
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#voluntaryexile
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class VoluntaryExile(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `voluntaryExile` criterion, triggered when a player causes a raid in a village (voluntary exile). */
fun AdvancementCriteria.voluntaryExile(name: String, block: VoluntaryExile.() -> Unit = {}) {
	criteria += VoluntaryExile(name).apply(block)
}
