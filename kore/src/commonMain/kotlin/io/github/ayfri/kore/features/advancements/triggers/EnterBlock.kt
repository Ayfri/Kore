package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.StateHolder
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Triggered when a player enters a block matching [blocks] and [state].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#enterblock
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class EnterBlock(
	override var player: EntityOrPredicates? = null,
	override var blocks: InlinableList<BlockOrTagArgument>? = null,
	override var state: Map<String, String>? = null,
) : AdvancementTriggerCondition(), BlocksTrigger, StateHolder

/** Add an `enterBlock` criterion, triggered when a player enters a specific block/state. */
fun AdvancementCriteria.enterBlock(name: String, block: EnterBlock.() -> Unit = {}) {
	criteria[name] = EnterBlock().apply(block)
}
