package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.types.Damage
import kotlinx.serialization.Serializable

@Serializable
data class EntityHurtPlayer(
	var damage: Damage? = null,
) : AdvancementTriggerCondition
