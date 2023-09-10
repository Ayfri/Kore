package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.worldgen.DimensionArgument
import kotlinx.serialization.Serializable

@Serializable
data class ChangedDimension(
	var from: DimensionArgument? = null,
	var to: DimensionArgument? = null,
) : AdvancementTriggerCondition
