package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.LocationPredicate
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable

/**
 * Passes when the location of the loot context origin, shifted by the offsets, matches [predicate].
 *
 * The offsets keep their camel-case JSON keys, which is what vanilla expects for this condition.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - location_check](https://minecraft.wiki/w/Predicate#location_check)
 */
@Serializable
data class LocationCheck(
	@JsonSerialName("offsetX")
	var offsetX: Int? = null,
	@JsonSerialName("offsetY")
	var offsetY: Int? = null,
	@JsonSerialName("offsetZ")
	var offsetZ: Int? = null,
	var predicate: LocationPredicate = LocationPredicate(),
) : PredicateCondition()

/** Adds a [LocationCheck] condition matching [predicate] at the origin shifted by the offsets. */
fun Predicate.locationCheck(offsetX: Int? = null, offsetY: Int? = null, offsetZ: Int? = null, predicate: LocationPredicate) {
	predicateConditions += LocationCheck(offsetX, offsetY, offsetZ, predicate)
}

/** Adds a [LocationCheck] condition matching the location built by [block] at the origin shifted by the offsets. */
fun Predicate.locationCheck(offsetX: Int? = null, offsetY: Int? = null, offsetZ: Int? = null, block: LocationPredicate.() -> Unit = {}) {
	predicateConditions += LocationCheck(offsetX, offsetY, offsetZ, LocationPredicate().apply(block))
}

/** Sets the location this condition matches. */
fun LocationCheck.predicate(block: LocationPredicate.() -> Unit) {
	predicate = LocationPredicate().apply(block)
}
