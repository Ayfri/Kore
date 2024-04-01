package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

@Serializable
data class ChanneledLightning(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var victims: List<EntityOrPredicates>? = null,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.channeledLightning(name: String, block: ChanneledLightning.() -> Unit = {}) {
	criteria += ChanneledLightning(name).apply(block)
}

fun ChanneledLightning.victim(block: EntityOrPredicates.() -> Unit) {
	victims = (victims ?: emptyList()) + EntityOrPredicates().apply(block)
}

fun ChanneledLightning.victims(vararg entities: Entity) {
	victims = entities.map { EntityOrPredicates(legacyEntity = it) }
}

fun ChanneledLightning.victims(vararg predicates: Predicate) {
	victims = predicates.map { EntityOrPredicates(predicateConditions = it.predicateConditions) }
}
