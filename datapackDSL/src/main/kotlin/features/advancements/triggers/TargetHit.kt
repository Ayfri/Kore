package features.advancements.triggers

import features.advancements.EntityOrPredicates
import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class TargetHit(
	var signalStrength: IntRangeOrIntJson? = null,
	var projectile: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
