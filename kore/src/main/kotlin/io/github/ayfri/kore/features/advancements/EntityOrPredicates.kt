package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

/**
 * Container for either an entity or predicate conditions.
 *
 * Docs: https://kore.ayfri.com/docs/advancements
 */
@Serializable(with = EntityOrPredicates.Companion.EntityOrPredicatesSerializer::class)
data class EntityOrPredicates(
	var legacyEntity: Entity? = null,
	var predicateConditions: List<PredicateCondition>? = null,
) {
	companion object {
		data object EntityOrPredicatesSerializer : ToStringSerializer<EntityOrPredicates>() {
			override fun serialize(encoder: Encoder, value: EntityOrPredicates) = when {
				value.legacyEntity != null -> encoder.encodeSerializableValue(Entity.serializer(), value.legacyEntity!!)
				value.predicateConditions != null -> encoder.encodeSerializableValue(
					ListSerializer(serializer<PredicateCondition>()),
					value.predicateConditions!!
				)

				else -> error("EntityOrPredicates must have either an Entity or predicates")
			}
		}
	}
}

/** Set the entity condition, deprecated, prefer using [conditions] instead. */
fun EntityOrPredicates.conditionEntity(entity: Entity) {
	legacyEntity = entity
}

/** Set the entity condition, deprecated, prefer using [conditions] instead. */
fun EntityOrPredicates.conditionEntity(entity: Entity.() -> Unit) {
	legacyEntity = Entity().apply(entity)
}

/** Set the predicate conditions. */
fun EntityOrPredicates.conditions(vararg conditions: PredicateCondition) {
	predicateConditions = conditions.toList()
}

/** Set the predicate conditions. */
fun EntityOrPredicates.conditions(conditions: Predicate.() -> Unit) {
	predicateConditions = Predicate().apply(conditions).predicateConditions
}
