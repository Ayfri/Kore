package features.advancements.triggers

import features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class BredAnimals(
	var child: EntityOrPredicates? = null,
	var parent: EntityOrPredicates? = null,
	var partner: EntityOrPredicates? = null
) : AdvancementTriggerCondition
