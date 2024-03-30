package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.Effect
import kotlinx.serialization.Serializable

@Serializable
data class EffectsChanged(
	var effects: Map<String, Effect>? = null,
	var source: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
