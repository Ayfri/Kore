package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.predicates.sub.Distance
import kotlinx.serialization.Serializable

@Serializable
data class NetherTravel(
	var distance: Distance? = null,
	var startPosition: Location? = null,
) : AdvancementTriggerCondition
