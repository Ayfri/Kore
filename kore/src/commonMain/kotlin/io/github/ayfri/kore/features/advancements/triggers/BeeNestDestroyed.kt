package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import io.github.ayfri.kore.features.predicates.sub.StateHolder
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Triggered when a bee nest or beehive matching [blocks] and [state] is destroyed.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#beenestdestroyed
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class BeeNestDestroyed(
	override var player: EntityOrPredicates? = null,
	override var blocks: InlinableList<BlockOrTagArgument>? = null,
	override var item: ItemStackPredicate? = null,
	var numBeesInside: IntRangeOrIntJson? = null,
	override var state: Map<String, String>? = null,
) : AdvancementTriggerCondition(), BlocksTrigger, ItemTrigger, StateHolder

/** Add a `beeNestDestroyed` criterion. */
fun AdvancementCriteria.beeNestDestroyed(
	name: String,
	item: ItemStackPredicate? = null,
	numBeesInside: IntRangeOrIntJson? = null,
	init: BeeNestDestroyed.() -> Unit,
) {
	criteria[name] = BeeNestDestroyed(item = item, numBeesInside = numBeesInside).apply(init)
}
