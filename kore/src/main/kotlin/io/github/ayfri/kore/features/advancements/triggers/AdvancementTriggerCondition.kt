package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.conditions
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Base sealed class for all advancement trigger conditions (one per vanilla trigger type).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement#JSON_format
 */
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

/** Set the entity condition, deprecated, prefer using [conditions] instead. */
fun AdvancementTriggerCondition.conditionEntity(entity: Entity) {
	this.conditions = EntityOrPredicates(legacyEntity = entity)
}

/** Set the entity condition, deprecated, prefer using [conditions] instead. */
fun AdvancementTriggerCondition.conditionEntity(entity: Entity.() -> Unit) {
	this.conditions = EntityOrPredicates(legacyEntity = Entity().apply(entity))
}

/** Set the predicate conditions. */
fun AdvancementTriggerCondition.conditions(vararg conditions: PredicateCondition) {
	this.conditions = EntityOrPredicates().conditions(*conditions)
}

/** Set the predicate conditions. */
fun AdvancementTriggerCondition.conditions(block: Predicate.() -> Unit) {
	this.conditions = EntityOrPredicates().conditions(block)
}
