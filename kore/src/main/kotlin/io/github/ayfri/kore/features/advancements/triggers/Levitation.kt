package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.Distance
import kotlinx.serialization.Serializable

@Serializable
data class Levitation(
	var distance: Distance? = null,
	var duration: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition
