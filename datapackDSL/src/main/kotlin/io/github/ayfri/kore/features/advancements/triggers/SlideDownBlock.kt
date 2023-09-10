package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.advancements.states.State
import kotlinx.serialization.Serializable

@Serializable
data class SlideDownBlock(
	var block: BlockArgument? = null,
	var state: Map<String, State<*>>? = null,
) : AdvancementTriggerCondition
