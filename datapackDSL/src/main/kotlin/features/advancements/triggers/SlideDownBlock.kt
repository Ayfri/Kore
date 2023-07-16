package features.advancements.triggers

import arguments.types.resources.BlockArgument
import features.advancements.states.State
import kotlinx.serialization.Serializable

@Serializable
data class SlideDownBlock(
	var block: BlockArgument? = null,
	var state: Map<String, State<*>>? = null,
) : AdvancementTriggerCondition
