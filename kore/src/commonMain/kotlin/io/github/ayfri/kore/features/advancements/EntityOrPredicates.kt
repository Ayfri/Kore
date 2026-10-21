package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.AllOf
import io.github.ayfri.kore.features.predicates.conditions.EntityProperties
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.sub.EntityPredicate
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * The one predicate a trigger entity field checks with the entity as `this`, written as the condition itself or as the
 * bare ID of a predicate file. Several conditions are merged into an `all_of`.
 *
 * ```
 * entity { condition { randomChance(0.5f) } }  // { "type": "minecraft:random_chance", "chance": 0.5 }
 * entity { condition(PredicateArgument("is_cow", "my_pack")) }  // "my_pack:is_cow"
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
@Serializable(with = EntityOrPredicates.Companion.EntityOrPredicatesSerializer::class)
data class EntityOrPredicates(var condition: PredicateCondition = AllOf()) {
	companion object {
		data object EntityOrPredicatesSerializer : InlineAutoSerializer<EntityOrPredicates, PredicateCondition>(
			PredicateCondition.serializer(),
			EntityOrPredicates::condition,
			::EntityOrPredicates,
			"entity_or_predicates",
		)
	}
}

/** Creates an [EntityOrPredicates] holding the one condition declared by [block], several ones being merged into an `all_of`. */
fun entityOrPredicates(block: Predicate.() -> Unit) = EntityOrPredicates(Predicate().apply(block).toCondition())

internal fun EntityPredicate.asEntityOrPredicates() = EntityOrPredicates(EntityProperties(predicate = this))

/** Check the entity against [entity], as an `entity_properties` condition on `this`. */
fun EntityOrPredicates.conditionEntity(entity: EntityPredicate) = apply {
	condition = EntityProperties(predicate = entity)
}

/** Check the entity against the predicate built by [entity], as an `entity_properties` condition on `this`. */
fun EntityOrPredicates.conditionEntity(entity: EntityPredicate.() -> Unit) = apply {
	condition = EntityProperties(predicate = EntityPredicate().apply(entity))
}

/** Set the condition, several ones being merged into an `all_of`. */
fun EntityOrPredicates.condition(vararg conditions: PredicateCondition) = apply {
	condition = Predicate(predicateConditions = conditions.toList()).toCondition()
}

/** Set the condition declared by [block], several ones being merged into an `all_of`. */
fun EntityOrPredicates.condition(block: Predicate.() -> Unit) = apply {
	condition = Predicate().apply(block).toCondition()
}

/** Set the condition to the predicate file [predicate], written as its bare ID. */
fun EntityOrPredicates.condition(predicate: PredicateArgument) = apply {
	condition = PredicateReference(predicate)
}
