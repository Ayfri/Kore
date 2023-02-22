package features.advancements.triggers

import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class ConstructBeacon(
	var level: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition
