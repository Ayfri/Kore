package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

@Serializable
data class UsedEnderEye(
	var distance: FloatRangeOrFloatJson? = null,
) : AdvancementTriggerCondition
