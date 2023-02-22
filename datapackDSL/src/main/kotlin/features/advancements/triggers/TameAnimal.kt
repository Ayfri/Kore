package features.advancements.triggers

import features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class TameAnimal(
	var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
