package features.advancements.types

import kotlinx.serialization.Serializable

@Serializable
data class DistanceEntry(
	var min: Float? = null,
	var max: Float? = null,
)

@Serializable
data class Distance(
	var absolute: DistanceEntry? = null,
	var horizontal: DistanceEntry? = null,
	var x: DistanceEntry? = null,
	var y: DistanceEntry? = null,
	var z: DistanceEntry? = null,
)

fun distance(init: Distance.() -> Unit = {}): Distance {
	val distance = Distance()
	distance.init()
	return distance
}
