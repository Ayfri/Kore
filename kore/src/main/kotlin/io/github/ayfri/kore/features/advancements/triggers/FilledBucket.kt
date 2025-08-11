package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when a player fills a bucket.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#filledbucket
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class FilledBucket(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

/** Add a `filledBucket` criterion, triggered when a player fills a bucket. */
fun AdvancementCriteria.filledBucket(name: String, block: FilledBucket.() -> Unit = {}) {
	criteria += FilledBucket(name).apply(block)
}

/** Set the item constraints. */
fun FilledBucket.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
