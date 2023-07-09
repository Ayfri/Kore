package features.advancements.triggers

import features.advancements.LocationOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class PlacedBlock(
	var location: LocationOrPredicates = LocationOrPredicates(),
) : AdvancementTriggerCondition
