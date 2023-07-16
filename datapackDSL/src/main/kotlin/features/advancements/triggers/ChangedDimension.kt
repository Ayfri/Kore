package features.advancements.triggers

import arguments.types.resources.DimensionArgument
import kotlinx.serialization.Serializable

@Serializable
data class ChangedDimension(
	var from: DimensionArgument? = null,
	var to: DimensionArgument? = null,
) : AdvancementTriggerCondition
