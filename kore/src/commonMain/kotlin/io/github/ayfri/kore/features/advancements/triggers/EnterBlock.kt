package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player enters a specific block and state.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#enterblock
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class EnterBlock(
	override var player: EntityOrPredicates? = null,
	var block: BlockArgument? = null,
	var state: Map<String, String>? = null,
) : AdvancementTriggerCondition()

/** Add an `enterBlock` criterion, triggered when a player enters a specific block/state. */
fun AdvancementCriteria.enterBlock(name: String, block: EnterBlock.() -> Unit = {}) {
	criteria[name] = EnterBlock().apply(block)
}

/** Requires the block state property [key] to equal [value]. */
fun EnterBlock.state(key: String, value: String) {
	state = mapOf(key to value)
}

/** Requires all the block state properties declared in [block]. */
fun EnterBlock.states(block: MutableMap<String, String>.() -> Unit) {
	state = buildMap(block)
}
