package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class LightningStrike(
	var bystander: EntityOrPredicates? = null,
	var lightning: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
