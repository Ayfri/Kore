package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class ConstructBeacon(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var level: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.constructBeacon(name: String, block: ConstructBeacon.() -> Unit = {}) {
	criteria += ConstructBeacon(name).apply(block)
}
