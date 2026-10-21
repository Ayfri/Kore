package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.StateHolder
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Triggered when sliding down a block matching [blocks] and [state].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#slidedownblock
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class SlideDownBlock(
	override var player: EntityOrPredicates? = null,
	override var blocks: InlinableList<BlockOrTagArgument>? = null,
	override var state: Map<String, String>? = null,
) : AdvancementTriggerCondition(), BlocksTrigger, StateHolder

/** Add a `slideDownBlock` criterion, triggered when sliding down a block. */
fun AdvancementCriteria.slideDownBlock(
	name: String,
	init: SlideDownBlock.() -> Unit,
) {
	criteria[name] = SlideDownBlock().apply(init)
}
