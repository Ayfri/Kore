package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.EntityProperties
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.sub.EntityPredicate
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import kotlinx.serialization.Serializable

/**
 * Triggered when lightning is channeled (e.g., via a trident with Channeling).
 *
 * Each of [victims] is one predicate checked against the struck entities, inline or the ID of a predicate file. A list
 * of victims can't be a single value and can't hold a predicate tag.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#channeledlightning
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class ChanneledLightning(
	override var player: EntityOrPredicates? = null,
	var victims: List<PredicateCondition>? = null,
) : AdvancementTriggerCondition()

/** Add a `channeledLightning` criterion, triggered when lightning is channeled. */
fun AdvancementCriteria.channeledLightning(name: String, block: ChanneledLightning.() -> Unit = {}) {
	criteria[name] = ChanneledLightning().apply(block)
}

/** Add one victim predicate. */
fun ChanneledLightning.victim(block: EntityOrPredicates.() -> Unit) {
	victims = (victims ?: emptyList()) + EntityOrPredicates().apply(block).condition
}

/** Add one victim predicate by the ID of a predicate file, written as its bare ID. */
fun ChanneledLightning.victim(predicate: PredicateArgument) {
	victims = (victims ?: emptyList()) + PredicateReference(predicate)
}

/** Replace the victims by entity predicates, each one an `entity_properties` condition on `this`. */
fun ChanneledLightning.victims(vararg entities: EntityPredicate) {
	victims = entities.map { EntityProperties(predicate = it) }
}

/** Replace the victims by predicate builders, see [Predicates](https://kore.ayfri.com/docs/data-driven/predicates). */
fun ChanneledLightning.victims(vararg predicates: Predicate) {
	victims = predicates.map { it.toCondition() }
}
