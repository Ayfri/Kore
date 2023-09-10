package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.types.Distance
import io.github.ayfri.kore.features.advancements.types.Location
import kotlinx.serialization.Serializable

@Serializable
data class NetherTravel(
	var distance: Distance? = null,
	var startPosition: Location? = null,
) : AdvancementTriggerCondition
