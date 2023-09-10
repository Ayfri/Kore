package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.types.Damage
import kotlinx.serialization.Serializable

@Serializable
data class PlayerHurtEntity(
	var damage: Damage? = null,
	var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
