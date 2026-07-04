package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered every tick (20 times per second) while conditions hold.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#tick
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class Tick(
	override var player: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `tick` criterion, triggered every tick (20 times per second) while conditions hold. */
fun AdvancementCriteria.tick(name: String, block: Tick.() -> Unit = {}) {
	criteria[name] = Tick().apply(block)
}
