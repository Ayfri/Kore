package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class EnterBlock(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var block: BlockArgument? = null,
	var states: Map<String, String>? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.enterBlock(name: String, block: EnterBlock.() -> Unit = {}) {
	criteria += EnterBlock(name).apply(block)
}

fun EnterBlock.states(block: MutableMap<String, String>.() -> Unit) {
	states = buildMap(block)
}
