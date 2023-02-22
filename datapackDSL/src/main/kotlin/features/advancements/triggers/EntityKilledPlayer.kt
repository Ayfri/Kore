package features.advancements.triggers

import features.advancements.EntityOrPredicates
import features.advancements.types.DamageSource
import kotlinx.serialization.Serializable

@Serializable
data class EntityKilledPlayer(
	var entity: EntityOrPredicates? = null,
	var killingBlow: DamageSource? = null,
) : AdvancementTriggerCondition
