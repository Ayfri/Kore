package features.advancements.triggers

import features.advancements.EntityOrPredicates
import features.advancements.types.Effect
import kotlinx.serialization.Serializable

@Serializable
data class EffectsChanged(
	var effects: Map<String, Effect>? = null,
	var source: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
