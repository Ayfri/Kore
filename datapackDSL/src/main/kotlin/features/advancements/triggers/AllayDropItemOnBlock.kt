package features.advancements.triggers

import features.advancements.LocationOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class AllayDropItemOnBlock(
	var location: LocationOrPredicates = LocationOrPredicates()
) : AdvancementTriggerCondition
