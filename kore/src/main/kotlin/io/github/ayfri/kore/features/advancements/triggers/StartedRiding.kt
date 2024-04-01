package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class StartedRiding(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.startedRiding(name: String, block: StartedRiding.() -> Unit = {}) {
	criteria += StartedRiding(name).apply(block)
}
