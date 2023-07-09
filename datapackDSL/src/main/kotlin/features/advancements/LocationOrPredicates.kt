package features.advancements

import features.advancements.types.Location
import features.predicates.conditions.PredicateCondition
import features.predicates.conditions.PredicateConditionSurrogate
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Encoder
import serializers.ToStringSerializer

@Serializable(with = LocationOrPredicates.Companion.LocationOrPredicatesSerializer::class)
data class LocationOrPredicates(
	var legacyLocation: Location? = null,
	var predicateConditions: List<PredicateCondition> = emptyList()
) {
	companion object {
		object LocationOrPredicatesSerializer : ToStringSerializer<LocationOrPredicates>() {
			override fun serialize(encoder: Encoder, value: LocationOrPredicates) {
				when {
					value.legacyLocation != null -> encoder.encodeSerializableValue(Location.serializer(), value.legacyLocation!!)
					value.predicateConditions != null -> encoder.encodeSerializableValue(
						ListSerializer(PredicateConditionSurrogate.Companion.PredicateConditionSerializer),
						value.predicateConditions
					)

					else -> error("LocationOrPredicates must have either a Location or predicates")
				}
			}
		}
	}
}
