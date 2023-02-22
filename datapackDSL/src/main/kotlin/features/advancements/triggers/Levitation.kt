package features.advancements.triggers

import features.advancements.serializers.IntRangeOrIntJson
import features.advancements.types.Distance
import kotlinx.serialization.Serializable

@Serializable
data class Levitation(
	var distance: Distance? = null,
	var duration: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition
