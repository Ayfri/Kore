package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a block is used with default interaction.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#defaultblockuse
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class DefaultBlockUse(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `defaultBlockUse` criterion, triggered when a block is used with default interaction. */
fun AdvancementCriteria.defaultBlockUse(name: String, block: DefaultBlockUse.() -> Unit = {}) {
	criteria += DefaultBlockUse(name).apply(block)
}
