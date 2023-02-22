package features.advancements.triggers

import features.advancements.types.Distance
import features.advancements.types.Location
import kotlinx.serialization.Serializable

@Serializable
data class NetherTravel(
	var distance: Distance? = null,
	var startPosition: Location? = null,
) : AdvancementTriggerCondition