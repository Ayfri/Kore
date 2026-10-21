package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.asEntityOrPredicates
import io.github.ayfri.kore.features.advancements.condition
import io.github.ayfri.kore.features.advancements.entityOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.EntityPredicate
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base sealed class for all advancement trigger conditions (one per vanilla trigger type).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement#JSON_format
 */
@GeneratedSealedSerializer
@Serializable(AdvancementTriggerCondition.Companion.AdvancementTriggerConditionDefaultSerializer::class)
sealed class AdvancementTriggerCondition {
	@SerialName("player")
	abstract var player: EntityOrPredicates?

	companion object {
		data object AdvancementTriggerConditionDefaultSerializer : NamespacedPolymorphicSerializer<AdvancementTriggerCondition>(
			advancementTriggerConditionSealedSerializer(),
			outputName = "trigger",
			moveIntoProperty = "conditions"
		)
	}
}

/** Check the player against [entity], as an `entity_properties` condition on `this`. */
fun AdvancementTriggerCondition.conditionEntity(entity: EntityPredicate) {
	player = entity.asEntityOrPredicates()
}

/** Check the player against the predicate built by [entity], as an `entity_properties` condition on `this`. */
fun AdvancementTriggerCondition.conditionEntity(entity: EntityPredicate.() -> Unit) {
	player = EntityPredicate().apply(entity).asEntityOrPredicates()
}

/** Set the player condition, several ones being merged into an `all_of`. */
fun AdvancementTriggerCondition.condition(vararg conditions: PredicateCondition) {
	player = EntityOrPredicates().condition(*conditions)
}

/** Set the player condition declared by [block], several ones being merged into an `all_of`. */
fun AdvancementTriggerCondition.condition(block: Predicate.() -> Unit) {
	player = entityOrPredicates(block)
}

/** Set the player condition to the predicate file [predicate], written as its bare ID. */
fun AdvancementTriggerCondition.condition(predicate: PredicateArgument) {
	player = EntityOrPredicates().condition(predicate)
}
