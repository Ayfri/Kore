package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Location
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Encoder

/**
 * Container for either a location or predicate conditions.
 *
 * Docs: https://kore.ayfri.com/docs/advancements
 */
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

/** Set the location condition. */
fun LocationOrPredicates.location(block: Location.() -> Unit) {
	legacyLocation = Location().apply(block)
}

/** Set the predicate conditions. */
fun LocationOrPredicates.predicate(block: Predicate.() -> Unit) {
	predicateConditions += Predicate().apply(block).predicateConditions
}

/** Set the predicate conditions. */
fun LocationOrPredicates.predicate(predicate: Predicate) {
	predicateConditions += predicate.predicateConditions
}
