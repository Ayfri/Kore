package features.advancements.triggers

import features.advancements.EntityOrPredicates
import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class KilledByCrossbow(
	var uniqueEntityTypes: IntRangeOrIntJson? = null,
	var victims: List<EntityOrPredicates>? = null,
) : AdvancementTriggerCondition
