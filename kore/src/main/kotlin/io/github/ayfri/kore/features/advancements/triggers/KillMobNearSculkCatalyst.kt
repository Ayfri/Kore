package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.predicates.sub.DamageSource
import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

@Serializable
data class KillMobNearSculkCatalyst(
	var entity: Entity? = null,
	var killingBlow: DamageSource? = null,
) : AdvancementTriggerCondition
