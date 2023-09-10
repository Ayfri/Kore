package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class TargetHit(
	var signalStrength: IntRangeOrIntJson? = null,
	var projectile: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
