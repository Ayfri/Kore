package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when a player sleeps in a bed.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#sleptinbed
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class SleptInBed(
	override var player: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `sleptInBed` criterion, triggered when a player sleeps in a bed. */
fun AdvancementCriteria.sleptInBed(name: String, block: SleptInBed.() -> Unit = {}) {
	criteria[name] = SleptInBed().apply(block)
}
