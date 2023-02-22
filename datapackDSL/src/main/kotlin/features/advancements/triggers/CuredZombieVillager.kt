package features.advancements.triggers

import features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class CuredZombieVillager(
	var villager: EntityOrPredicates? = null,
	var zombie: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
