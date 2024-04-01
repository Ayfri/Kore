package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Location
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Encoder

@Serializable(with = LocationOrPredicates.Companion.LocationOrPredicatesSerializer::class)
data class LocationOrPredicates(
	var legacyLocation: Location? = null,
	var predicateConditions: List<PredicateCondition> = emptyList(),
) {
	companion object {
		object LocationOrPredicatesSerializer : ToStringSerializer<LocationOrPredicates>() {
			override fun serialize(encoder: Encoder, value: LocationOrPredicates) {
				when {
					value.legacyLocation != null -> encoder.encodeSerializableValue(Location.serializer(), value.legacyLocation!!)
					else -> encoder.encodeSerializableValue(
						ListSerializer(kotlinx.serialization.serializer<PredicateCondition>()),
						value.predicateConditions
					)
				}
			}
		}
	}
}

fun LocationOrPredicates.location(block: Location.() -> Unit) {
	legacyLocation = Location().apply(block)
}

fun LocationOrPredicates.predicate(block: Predicate.() -> Unit) {
	predicateConditions += Predicate().apply(block).predicateConditions
}

fun LocationOrPredicates.predicate(predicate: Predicate) {
	predicateConditions += predicate.predicateConditions
}
