package features.advancements.triggers

import arguments.Argument
import features.advancements.states.State
import kotlinx.serialization.Serializable

@Serializable
data class SlideDownBlock(
	var block: Argument.Block? = null,
	var state: Map<String, State<*>>? = null,
) : AdvancementTriggerCondition
