package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.predicates.sub.Damage
import kotlinx.serialization.Serializable

@Serializable
data class EntityHurtPlayer(
	var damage: Damage? = null,
) : AdvancementTriggerCondition
