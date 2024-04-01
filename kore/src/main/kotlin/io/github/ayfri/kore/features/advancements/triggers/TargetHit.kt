package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class TargetHit(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var signalStrength: IntRangeOrIntJson? = null,
	var projectile: EntityOrPredicates? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.targetHit(name: String, block: TargetHit.() -> Unit = {}) {
	criteria += TargetHit(name).apply(block)
}

fun TargetHit.projectile(block: EntityOrPredicates.() -> Unit) {
	projectile = EntityOrPredicates().apply(block)
}
