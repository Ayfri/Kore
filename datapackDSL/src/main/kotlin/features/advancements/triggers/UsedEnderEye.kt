package features.advancements.triggers

import features.advancements.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

@Serializable
data class UsedEnderEye(
	var distance: FloatRangeOrFloatJson? = null,
) : AdvancementTriggerCondition
