package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder

/**
 * Container for either an entity or predicate conditions.
 *
 * Docs: https://kore.ayfri.com/docs/advancements
 */
@Serializable(with = EntityOrPredicates.Companion.EntityOrPredicatesSerializer::class)
data class EntityOrPredicates(
	var legacyEntity: Entity? = null,
	var predicateConditions: PredicateAsList = Predicate(),
) {
	companion object {
		data object EntityOrPredicatesSerializer : ToStringSerializer<EntityOrPredicates>() {
			override fun serialize(encoder: Encoder, value: EntityOrPredicates) = when {
				value.legacyEntity != null -> encoder.encodeSerializableValue(Entity.serializer(), value.legacyEntity!!)
				else -> encoder.encodeSerializableValue(
					Predicate.Companion.PredicateAsListSerializer,
					value.predicateConditions
				)
			}
		}
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
