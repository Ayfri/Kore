package io.github.ayfri.kore.features.advancements

// import features.predicates.conditions.PredicateConditionSurrogate
import io.github.ayfri.kore.features.advancements.types.Entity
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

@Serializable(with = EntityOrPredicates.Companion.EntityOrPredicatesSerializer::class)
data class EntityOrPredicates(
	var legacyEntity: Entity? = null,
	var predicateConditions: List<PredicateCondition>? = null,
) {
	companion object {
		object EntityOrPredicatesSerializer : ToStringSerializer<EntityOrPredicates>() {
			override fun serialize(encoder: Encoder, value: EntityOrPredicates) {
				when {
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
}
