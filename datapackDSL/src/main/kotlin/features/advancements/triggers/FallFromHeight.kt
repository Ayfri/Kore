package features.advancements.triggers

import features.advancements.types.Distance
import features.advancements.types.Location
import kotlinx.serialization.Serializable

@Serializable
data class FallFromHeight(
	var startPosition: Location? = null,
	var distance: Distance? = null,
) : AdvancementTriggerCondition
