package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.types.DamageSource
import io.github.ayfri.kore.features.advancements.types.Entity
import kotlinx.serialization.Serializable

@Serializable
data class KillMobNearSculkCatalyst(
	var entity: Entity? = null,
	var killingBlow: DamageSource? = null,
) : AdvancementTriggerCondition
