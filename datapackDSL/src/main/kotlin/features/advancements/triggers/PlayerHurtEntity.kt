package features.advancements.triggers

import features.advancements.EntityOrPredicates
import features.advancements.types.Damage
import kotlinx.serialization.Serializable

@Serializable
data class PlayerHurtEntity(
	var damage: Damage? = null,
	var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
