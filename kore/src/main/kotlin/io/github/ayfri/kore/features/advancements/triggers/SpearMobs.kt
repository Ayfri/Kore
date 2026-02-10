package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player spears mobs.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#spearmobs
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class SpearMobs(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var count: Int? = null,
) : AdvancementTriggerCondition()

/** Add a `spearMobs` criterion, triggered when a player spears mobs. */
fun AdvancementCriteria.spearMobs(name: String, block: SpearMobs.() -> Unit = {}) {
	criteria += SpearMobs(name).apply(block)
}
