package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class FilledBucket(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.filledBucket(name: String, block: FilledBucket.() -> Unit = {}) {
	criteria += FilledBucket(name).apply(block)
}

fun FilledBucket.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
