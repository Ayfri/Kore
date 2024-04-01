package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class DefaultBlockUse(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.defaultBlockUse(name: String, block: DefaultBlockUse.() -> Unit = {}) {
	criteria += DefaultBlockUse(name).apply(block)
}
