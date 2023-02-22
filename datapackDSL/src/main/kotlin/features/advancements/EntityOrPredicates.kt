package features.advancements

import features.advancements.types.Entity
import features.predicates.Predicate
import features.predicates.PredicateFile
import features.predicates.toPredicateFile
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.ToStringSerializer

@Serializable(with = EntityOrPredicates.Companion.AdvancementConditionsSerializer::class)
data class EntityOrPredicates(
	var legacyEntity: Entity? = null,
	var predicates: Collection<Predicate>? = null,
) {
	companion object {
		object AdvancementConditionsSerializer : ToStringSerializer<EntityOrPredicates>() {
			override fun serialize(encoder: Encoder, value: EntityOrPredicates) {
				when {
					value.legacyEntity != null -> encoder.encodeSerializableValue(Entity.serializer(), value.legacyEntity!!)
					value.predicates != null -> encoder.encodeSerializableValue(
						PredicateFile.Companion.PredicateFileSerializer,
						value.predicates!!.toPredicateFile()
					)

					else -> error("AdvancementConditions must have either an Entity or predicates")
				}
			}
		}
	}
}

fun conditionEntity(block: () -> Entity) = EntityOrPredicates(legacyEntity = block())
fun conditionPredicate(block: () -> Predicate) = EntityOrPredicates(predicates = listOf(block()))
fun conditionPredicates(block: () -> Collection<Predicate>) = EntityOrPredicates(predicates = block())
