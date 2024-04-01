package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.states.State
import kotlinx.serialization.Serializable

@Serializable
data class SlideDownBlock(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var block: BlockArgument? = null,
	var state: Map<String, State<*>>? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.slideDownBlock(
	name: String,
	block: BlockArgument? = null,
	state: Map<String, State<*>>? = null,
	init: SlideDownBlock.() -> Unit,
) {
	criteria += SlideDownBlock(name, block = block, state = state).apply(init)
}
