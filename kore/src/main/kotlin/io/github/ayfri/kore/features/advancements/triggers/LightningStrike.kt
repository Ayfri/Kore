package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

/**
 * Triggered when lightning strikes.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#lightningstrike
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class LightningStrike(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var bystander: EntityOrPredicates? = null,
	var lightning: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `lightningStrike` criterion, triggered when lightning strikes. */
fun AdvancementCriteria.lightningStrike(name: String, block: LightningStrike.() -> Unit = {}) {
	criteria += LightningStrike(name).apply(block)
}

/** Set the bystander constraints. */
fun LightningStrike.bystander(block: EntityOrPredicates.() -> Unit) {
	bystander = EntityOrPredicates().apply(block)
}

/** Set the lightning entity constraints. */
fun LightningStrike.lightning(block: EntityOrPredicates.() -> Unit) {
	lightning = EntityOrPredicates().apply(block)
}
