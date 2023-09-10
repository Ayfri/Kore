package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class SummonedEntity(
	var entity: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
