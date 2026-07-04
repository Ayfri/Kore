package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when sliding down a block.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#slidedownblock
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class SlideDownBlock(
	override var player: EntityOrPredicates? = null,
	var block: BlockArgument? = null,
) : AdvancementTriggerCondition()

/** Add a `slideDownBlock` criterion, triggered when sliding down a block. */
fun AdvancementCriteria.slideDownBlock(
	name: String,
	block: BlockArgument? = null,
	init: SlideDownBlock.() -> Unit,
) {
	criteria[name] = SlideDownBlock(block = block).apply(init)
}
