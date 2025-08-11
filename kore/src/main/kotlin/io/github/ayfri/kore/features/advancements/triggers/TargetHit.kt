package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

/**
 * Triggered when a target block is hit.
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#targethit
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class TargetHit(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var signalStrength: IntRangeOrIntJson? = null,
	var projectile: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

/** Add a `targetHit` criterion, triggered when a target block is hit. */
fun AdvancementCriteria.targetHit(name: String, block: TargetHit.() -> Unit = {}) {
	criteria += TargetHit(name).apply(block)
}

/** Set the projectile constraints. */
fun TargetHit.projectile(block: EntityOrPredicates.() -> Unit) {
	projectile = EntityOrPredicates().apply(block)
}
