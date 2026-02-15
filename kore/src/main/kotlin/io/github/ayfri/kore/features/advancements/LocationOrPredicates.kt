package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Location
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.Serializable

/**
 * Container for either a location or predicate conditions.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
@Serializable(with = LocationOrPredicates.Companion.LocationOrPredicatesSerializer::class)
data class LocationOrPredicates(
	var legacyLocation: Location? = null,
	var predicateConditions: List<PredicateCondition>? = null,
) {
	companion object {
		data object LocationOrPredicatesSerializer : EitherInlineSerializer<LocationOrPredicates>(
			LocationOrPredicates::class,
			LocationOrPredicates::legacyLocation,
			LocationOrPredicates::predicateConditions,
		)
	}
}

/** Set the location condition. */
fun LocationOrPredicates.location(block: Location.() -> Unit) {
	legacyLocation = Location().apply(block)
}

/** Set the predicate conditions. */
fun LocationOrPredicates.predicate(block: Predicate.() -> Unit) {
	predicateConditions = (predicateConditions ?: emptyList()) + Predicate().apply(block).predicateConditions
}

/** Set the predicate conditions. */
fun LocationOrPredicates.predicate(predicate: Predicate) {
	predicateConditions = (predicateConditions ?: emptyList()) + predicate.predicateConditions
}
