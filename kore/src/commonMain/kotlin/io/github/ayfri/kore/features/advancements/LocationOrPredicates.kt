package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.AllOf
import io.github.ayfri.kore.features.predicates.conditions.LocationCheck
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.sub.LocationPredicate
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * The one predicate a trigger location field checks, written as the condition itself or as the bare ID of a predicate
 * file. A location predicate is wrapped in a `location_check` and several conditions are merged into an `all_of`.
 *
 * ```
 * location { location { biomes(Biomes.PLAINS) } }  // { "type": "minecraft:location_check", "predicate": { "biomes": ... } }
 * location { predicate(PredicateArgument("is_farm", "my_pack")) }  // "my_pack:is_farm"
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
@Serializable(with = LocationOrPredicates.Companion.LocationOrPredicatesSerializer::class)
data class LocationOrPredicates(var condition: PredicateCondition = AllOf()) {
	companion object {
		data object LocationOrPredicatesSerializer : InlineAutoSerializer<LocationOrPredicates, PredicateCondition>(
			PredicateCondition.serializer(),
			LocationOrPredicates::condition,
			::LocationOrPredicates,
			"location_or_predicates",
		)
	}
}

/** Check the location against [block], as a `location_check` condition. */
fun LocationOrPredicates.location(block: LocationPredicate.() -> Unit) {
	condition = LocationCheck(predicate = LocationPredicate().apply(block))
}

/** Set the condition declared by [block], several ones being merged into an `all_of`. */
fun LocationOrPredicates.predicate(block: Predicate.() -> Unit) {
	condition = Predicate().apply(block).toCondition()
}

/** Set the condition to [predicate], several conditions being merged into an `all_of`. */
fun LocationOrPredicates.predicate(predicate: Predicate) {
	condition = predicate.toCondition()
}

/** Set the condition to the predicate file [predicate], written as its bare ID. */
fun LocationOrPredicates.predicate(predicate: PredicateArgument) {
	condition = PredicateReference(predicate)
}
