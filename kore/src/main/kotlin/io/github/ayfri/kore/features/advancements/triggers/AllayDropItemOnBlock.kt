package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.LocationOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class AllayDropItemOnBlock(
	var location: LocationOrPredicates = LocationOrPredicates()
) : AdvancementTriggerCondition
