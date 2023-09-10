package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class KilledByCrossbow(
	var uniqueEntityTypes: IntRangeOrIntJson? = null,
	var victims: List<EntityOrPredicates>? = null,
) : AdvancementTriggerCondition
