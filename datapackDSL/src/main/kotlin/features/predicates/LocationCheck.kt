package features.predicates

import features.advancements.types.Location
import kotlinx.serialization.Serializable

@Serializable
data class LocationCheck(
	var offsetX: Int? = null,
	var offsetY: Int? = null,
	var offsetZ: Int? = null,
	var predicate: Location? = null,
) : Predicate
