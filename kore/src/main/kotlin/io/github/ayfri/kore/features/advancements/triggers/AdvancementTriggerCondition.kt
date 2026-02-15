package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.conditions
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base sealed class for all advancement trigger conditions (one per vanilla trigger type).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement#JSON_format
 */
@Serializable(AdvancementTriggerCondition.Companion.AdvancementTriggerConditionDefaultSerializer::class)
sealed class AdvancementTriggerCondition {
	@SerialName("player")
	abstract var player: EntityOrPredicates?

	companion object {
		data object AdvancementTriggerConditionDefaultSerializer : NamespacedPolymorphicSerializer<AdvancementTriggerCondition>(
			kClass = AdvancementTriggerCondition::class,
			outputName = "trigger",
			moveIntoProperty = "conditions"
		)
	}
}

/** Set the entity condition, deprecated, prefer using [conditions] instead. */
fun AdvancementTriggerCondition.conditionEntity(entity: Entity) {
	player = EntityOrPredicates(legacyEntity = entity)
}

/** Set the entity condition, deprecated, prefer using [conditions] instead. */
fun AdvancementTriggerCondition.conditionEntity(entity: Entity.() -> Unit) {
	player = EntityOrPredicates(legacyEntity = Entity().apply(entity))
}

/** Set the predicate conditions. */
fun AdvancementTriggerCondition.conditions(vararg conditions: PredicateCondition) {
	this.player = EntityOrPredicates().conditions(*conditions)
}

/** Set the predicate conditions. */
fun AdvancementTriggerCondition.conditions(block: Predicate.() -> Unit) {
	player = EntityOrPredicates().conditions(block)
}
