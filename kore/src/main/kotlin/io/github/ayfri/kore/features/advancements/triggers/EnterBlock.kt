package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player enters a specific block and state.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#enterblock
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class EnterBlock(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var block: BlockArgument? = null,
	var states: Map<String, String>? = null,
) : AdvancementTriggerCondition()

/** Add an `enterBlock` criterion, triggered when a player enters a specific block/state. */
fun AdvancementCriteria.enterBlock(name: String, block: EnterBlock.() -> Unit = {}) {
	criteria += EnterBlock(name).apply(block)
}

/** Set the required block state map. */
fun EnterBlock.states(block: MutableMap<String, String>.() -> Unit) {
	states = buildMap(block)
}
