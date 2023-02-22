package features.predicates

import kotlinx.serialization.Serializable

@Serializable
data class WeatherCheck(
	var raining: Boolean,
	var thundering: Boolean,
) : Predicate
