package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class LightningStrike(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var bystander: EntityOrPredicates? = null,
	var lightning: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.lightningStrike(name: String, block: LightningStrike.() -> Unit = {}) {
	criteria += LightningStrike(name).apply(block)
}

fun LightningStrike.bystander(block: EntityOrPredicates.() -> Unit) {
	bystander = EntityOrPredicates().apply(block)
}

fun LightningStrike.lightning(block: EntityOrPredicates.() -> Unit) {
	lightning = EntityOrPredicates().apply(block)
}
