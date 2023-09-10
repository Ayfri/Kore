package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class CuredZombieVillager(
	var villager: EntityOrPredicates? = null,
	var zombie: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
