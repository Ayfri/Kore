package features.advancements.triggers

import features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class LightningStrike(
	var bystander: EntityOrPredicates? = null,
	var lightning: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
