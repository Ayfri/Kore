package features.advancements

import features.advancements.types.Entity
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.PredicateConditionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Encoder
import serializers.ToStringSerializer

@Serializable(with = EntityOrPredicates.Companion.AdvancementConditionsSerializer::class)
data class EntityOrPredicates(
	var legacyEntity: Entity? = null,
	var predicateConditions: List<PredicateCondition>? = null,
) {
	companion object {
		object AdvancementConditionsSerializer : ToStringSerializer<EntityOrPredicates>() {
			override fun serialize(encoder: Encoder, value: EntityOrPredicates) {
				when {
					value.legacyEntity != null -> encoder.encodeSerializableValue(Entity.serializer(), value.legacyEntity!!)
					value.predicateConditions != null -> encoder.encodeSerializableValue(
						ListSerializer(PredicateConditionSerializer),
						value.predicateConditions!!
					)

					else -> error("AdvancementConditions must have either an Entity or predicates")
				}
			}
		}
	}
}
