package features.advancements.triggers

import features.advancements.types.Damage
import kotlinx.serialization.Serializable

@Serializable
data class EntityHurtPlayer(
	var damage: Damage? = null,
) : AdvancementTriggerCondition
