package features.advancements.triggers

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class ChangedDimension(
	var from: Argument.Dimension? = null,
	var to: Argument.Dimension? = null,
) : AdvancementTriggerCondition
