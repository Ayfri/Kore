package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Prevents the advancement from being achieved. Useful for creating advancements that should only trigger functions.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#impossible
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class Impossible(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add an `impossible` criterion, prevents the advancement from being achieved. */
fun AdvancementCriteria.impossible(name: String, block: Impossible.() -> Unit = {}) {
	criteria += Impossible(name).apply(block)
}
