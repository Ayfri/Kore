package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

/**
 * Triggered when a crossbow is shot.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#shotcrossbow
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class ShotCrossbow(
	override var player: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition()

/** Add a `shotCrossbow` criterion. */
fun AdvancementCriteria.shotCrossbow(name: String, block: ShotCrossbow.() -> Unit = {}) {
	criteria[name] = ShotCrossbow().apply(block)
}

/** Set the crossbow item constraints. */
fun ShotCrossbow.item(block: ItemStack.() -> Unit) {
	item = ItemStack().apply(block)
}
