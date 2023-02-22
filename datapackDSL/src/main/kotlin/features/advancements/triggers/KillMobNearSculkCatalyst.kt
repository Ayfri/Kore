package features.advancements.triggers

import features.advancements.types.DamageSource
import features.advancements.types.Entity
import kotlinx.serialization.Serializable

@Serializable
data class KillMobNearSculkCatalyst(
	var entity: Entity? = null,
	var killingBlow: DamageSource? = null,
) : AdvancementTriggerCondition
