package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class VoluntaryExile(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.voluntaryExile(name: String, block: VoluntaryExile.() -> Unit = {}) {
	criteria += VoluntaryExile(name).apply(block)
}
