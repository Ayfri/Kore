package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(AdvancementTriggerCondition.Companion.AdvancementTriggerConditionSerializer::class)
sealed class AdvancementTriggerCondition {
	abstract var name: String
	abstract var conditions: EntityOrPredicates?

	companion object {
		data object AdvancementTriggerConditionSerializer : NamespacedPolymorphicSerializer<AdvancementTriggerCondition>(
			AdvancementTriggerCondition::class,
			outputName = "trigger",
			moveIntoProperty = "conditions"
		)
	}
}

fun AdvancementTriggerCondition.conditionEntity(entity: Entity) {
	this.conditions = EntityOrPredicates(legacyEntity = entity)
}

fun AdvancementTriggerCondition.conditionEntity(entity: Entity.() -> Unit) {
	this.conditions = EntityOrPredicates(legacyEntity = Entity().apply(entity))
}

fun AdvancementTriggerCondition.conditions(vararg conditions: PredicateCondition) {
	this.conditions = EntityOrPredicates(predicateConditions = conditions.toList())
}

fun AdvancementTriggerCondition.conditions(conditions: Predicate.() -> Unit) {
	this.conditions = EntityOrPredicates(predicateConditions = Predicate().apply(conditions).predicateConditions)
}
