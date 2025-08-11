package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player starts riding an entity.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#startedriding
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class StartedRiding(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `startedRiding` criterion. */
fun AdvancementCriteria.startedRiding(name: String, block: StartedRiding.() -> Unit = {}) {
	criteria += StartedRiding(name).apply(block)
}
