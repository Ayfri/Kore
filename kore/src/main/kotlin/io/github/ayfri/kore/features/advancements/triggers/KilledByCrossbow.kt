package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class KilledByCrossbow(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var uniqueEntityTypes: IntRangeOrIntJson? = null,
	var victims: List<EntityOrPredicates>? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.killedByCrossbow(name: String, block: KilledByCrossbow.() -> Unit = {}) {
	criteria += KilledByCrossbow(name).apply(block)
}

fun KilledByCrossbow.uniqueEntityTypes(block: IntRangeOrIntJson.() -> Unit) {
	uniqueEntityTypes = IntRangeOrIntJson().apply(block)
}

fun KilledByCrossbow.victim(block: EntityOrPredicates.() -> Unit) {
	victims = (victims ?: emptyList()) + EntityOrPredicates().apply(block)
}

fun KilledByCrossbow.victims(block: MutableList<EntityOrPredicates>.() -> Unit) {
	victims = mutableListOf<EntityOrPredicates>().apply(block)
}
