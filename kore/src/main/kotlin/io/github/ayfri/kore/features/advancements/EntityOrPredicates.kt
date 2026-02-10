package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.Serializable

/**
 * Container for either an entity or predicate conditions.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
@Serializable(with = EntityOrPredicates.Companion.EntityOrPredicatesSerializer::class)
data class EntityOrPredicates(
	var legacyEntity: Entity? = null,
	@Serializable(with = Predicate.Companion.PredicateAsListSerializer::class)
	var predicateConditions: PredicateAsList = Predicate(),
) {
	companion object {
		data object EntityOrPredicatesSerializer : EitherInlineSerializer<EntityOrPredicates>(
			EntityOrPredicates::class,
			EntityOrPredicates::legacyEntity,
			EntityOrPredicates::predicateConditions,
		)
	}
}

/** Set the entity condition, deprecated, prefer using [conditions] instead. */
fun EntityOrPredicates.conditionEntity(entity: Entity) = apply {
	legacyEntity = entity
}

/** Set the entity condition, deprecated, prefer using [conditions] instead. */
fun EntityOrPredicates.conditionEntity(entity: Entity.() -> Unit) = apply {
	legacyEntity = Entity().apply(entity)
}

/** Set the predicate conditions. */
fun EntityOrPredicates.conditions(vararg conditions: PredicateCondition) = apply {
	predicateConditions = Predicate(predicateConditions = conditions.toList())
}

/** Set the predicate conditions. */
fun EntityOrPredicates.conditions(conditions: Predicate.() -> Unit) = apply {
	predicateConditions = Predicate().apply(conditions)
}
