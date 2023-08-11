package features.predicates.conditions

import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class WeatherCheck(
	var raining: Boolean? = null,
	var thundering: Boolean? = null,
) : PredicateCondition()

fun Predicate.weatherCheck(raining: Boolean? = null, thundering: Boolean? = null) {
	predicateConditions += WeatherCheck(raining, thundering)
}
