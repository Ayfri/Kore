package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when a bee nest is destroyed.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#beeenestdestroyed
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class BeeNestDestroyed(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var block: BlockArgument? = null,
	var item: ItemStack? = null,
	var numBeesInside: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition()

/** Add a `beeNestDestroyed` criterion. */
fun AdvancementCriteria.beeNestDestroyed(
	name: String,
	block: BlockArgument? = null,
	item: ItemStack? = null,
	numBeesInside: IntRangeOrIntJson? = null,
	init: BeeNestDestroyed.() -> Unit,
) {
	criteria += BeeNestDestroyed(name, block = block, item = item, numBeesInside = numBeesInside).apply(init)
}

/** Define the item used in the destruction. */
fun BeeNestDestroyed.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
