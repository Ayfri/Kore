package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.Distance
import kotlinx.serialization.Serializable

@Serializable
data class Levitation(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var distance: Distance? = null,
	var duration: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.levitation(name: String, block: Levitation.() -> Unit = {}) {
	criteria += Levitation(name).apply(block)
}

fun Levitation.distance(block: Distance.() -> Unit) {
	distance = Distance().apply(block)
}
