package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Effect
import kotlinx.serialization.Serializable

@Serializable
data class EffectsChanged(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var effects: Map<String, Effect>? = null,
	var source: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.effectsChanged(name: String, block: EffectsChanged.() -> Unit = {}) {
	criteria += EffectsChanged(name).apply(block)
}

fun EffectsChanged.effects(block: Map<String, Effect>.() -> Unit) {
	effects = buildMap(block)
}

fun EffectsChanged.source(block: EntityOrPredicates.() -> Unit) {
	source = EntityOrPredicates().apply(block)
}
