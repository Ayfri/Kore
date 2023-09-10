package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class ConstructBeacon(
	var level: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition
