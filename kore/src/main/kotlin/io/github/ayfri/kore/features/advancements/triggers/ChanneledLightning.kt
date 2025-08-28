package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

/**
 * Triggered when lightning is channeled (e.g., via a trident with Channeling).
 *
 * Docs: https://kore.ayfri.com/docs/advancements/triggers#channeledlightning
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class ChanneledLightning(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var victims: List<EntityOrPredicates>? = null,
) : AdvancementTriggerCondition()

/** Add a `channeledLightning` criterion, triggered when lightning is channeled. */
fun AdvancementCriteria.channeledLightning(name: String, block: ChanneledLightning.() -> Unit = {}) {
	criteria += ChanneledLightning(name).apply(block)
}

/** Add one victim predicate. */
fun ChanneledLightning.victim(block: EntityOrPredicates.() -> Unit) {
	victims = (victims ?: emptyList()) + EntityOrPredicates().apply(block)
}

/** Add victims by entity instances, deprecated, prefer using predicates instead. */
fun ChanneledLightning.victims(vararg entities: Entity) {
	victims = entities.map { EntityOrPredicates(legacyEntity = it) }
}

/** Add victims by predicate builders, see [Predicates](https://kore.ayfri.com/docs/predicates). */
fun ChanneledLightning.victims(vararg predicates: Predicate) {
	victims = predicates.map { EntityOrPredicates(predicateConditions = it) }
}
